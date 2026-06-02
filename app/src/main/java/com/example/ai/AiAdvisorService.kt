package com.example.ai

import com.example.BuildConfig
import com.example.data.Transaction
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

@JsonClass(generateAdapter = true)
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>
)

@JsonClass(generateAdapter = true)
data class OpenAiMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenAiResponse(
    val choices: List<OpenAiChoice>?
)

@JsonClass(generateAdapter = true)
data class OpenAiChoice(
    val message: OpenAiMessage?
)

object AiApiClient {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

class AiAdvisorService {
    suspend fun generateFinancialAdvice(
        transactions: List<Transaction>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val providers = listOf(
            AiProvider(
                name = "Groq",
                url = "https://api.groq.com/openai/v1/chat/completions",
                key = BuildConfig.GROQ_API_KEY,
                model = "llama3-70b-8192"
            ),
            AiProvider(
                name = "DeepSeek",
                url = "https://api.deepseek.com/chat/completions",
                key = BuildConfig.DEEPSEEK_API_KEY,
                model = "deepseek-chat"
            ),
            AiProvider(
                name = "Mistral",
                url = "https://api.mistral.ai/v1/chat/completions",
                key = BuildConfig.MISTRAL_API_KEY,
                model = "mistral-small-latest"
            )
        ).filter { it.key.isNotBlank() }

        if (providers.isEmpty()) {
            return@withContext "Please configure at least one API key (GROQ, DEEPSEEK, or MISTRAL) in the AI Studio Settings (Secrets Panel)."
        }

        val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = income - expense
        
        val categoryExpenses = transactions.filter { it.type == "Expense" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
        val topCategory = categoryExpenses.maxByOrNull { it.value }?.key ?: "None"

        val systemPrompt = "You are a helpful, professional Thai financial advisor. Keep answers concise."
        val prompt = """
            You are a personal financial advisor in Thai. Analyze the financial data below and answer the user.
            
            Current Financial Context:
            - Income this month: $income THB
            - Expense this month: $expense THB
            - Balance: $balance THB
            - Top Expense Category: $topCategory
            - Recent Transactions count: ${transactions.size}
            
            User message: $userMessage
            
            Answer concisely and provide real, helpful advice in clear Thai.
        """.trimIndent()

        val requestAdapter = AiApiClient.moshi.adapter(OpenAiRequest::class.java)
        val responseAdapter = AiApiClient.moshi.adapter(OpenAiResponse::class.java)

        for (i in providers.indices) {
            val provider = providers[i]
            val requestBody = OpenAiRequest(
                model = provider.model,
                messages = listOf(
                    OpenAiMessage(role = "system", content = systemPrompt),
                    OpenAiMessage(role = "user", content = prompt)
                )
            )

            val jsonBody = requestAdapter.toJson(requestBody)
            val request = Request.Builder()
                .url(provider.url)
                .addHeader("Authorization", "Bearer ${provider.key}")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            try {
                AiApiClient.client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body?.string()
                        if (bodyString != null) {
                            val aiResponse = responseAdapter.fromJson(bodyString)
                            val answer = aiResponse?.choices?.firstOrNull()?.message?.content
                            if (answer != null) {
                                return@withContext answer
                            }
                        }
                    } else {
                        if (response.code == 429 || response.code >= 500) {
                            if (i == providers.lastIndex) return@withContext "AI Error from ${provider.name}: ${response.code}"
                            continue // Try next provider
                        } else {
                            if (i == providers.lastIndex) return@withContext "AI Error: ${response.code} from ${provider.name}"
                        }
                    }
                }
            } catch (e: IOException) {
                if (i == providers.lastIndex) return@withContext "Error reaching AI Advisors: ${e.message}"
                continue
            }
        }
        
        "All AI providers are currently unavailable."
    }

    data class AiProvider(val name: String, val url: String, val key: String, val model: String)
}
