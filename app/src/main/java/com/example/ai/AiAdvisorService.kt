package com.example.ai

import com.example.BuildConfig
import com.example.data.Transaction
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Using Moshi instead of kotlinx.serialization as it's already provided locally
@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): retrofit2.Response<GenerateContentResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class AiAdvisorService {
    suspend fun generateFinancialAdvice(
        transactions: List<Transaction>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        val apiKeys = listOf(
            BuildConfig.GEMINI_API_KEY_1,
            BuildConfig.GEMINI_API_KEY_2,
            BuildConfig.GEMINI_API_KEY_3
        ).filter { it.isNotBlank() && !it.contains("MY_GEMINI_API_KEY") }.toMutableList()
        
        // Support fallback to standard GEMINI_API_KEY
        val standardKey = BuildConfig.GEMINI_API_KEY
        if (standardKey.isNotBlank() && !standardKey.contains("MY_GEMINI_API_KEY") && !apiKeys.contains(standardKey)) {
            apiKeys.add(0, standardKey)
        }

        if (apiKeys.isEmpty()) {
            return@withContext "Please configure your Gemini API key in the AI Studio Settings (Secrets Panel)."
        }

        val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = income - expense
        
        val categoryExpenses = transactions.filter { it.type == "Expense" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
        val topCategory = categoryExpenses.maxByOrNull { it.value }?.key ?: "None"

        val contextPrompt = """
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

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = contextPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are a helpful, professional Thai financial advisor. Keep answers concise.")))
        )
        
        for (i in apiKeys.indices) {
            try {
                val response = RetrofitClient.service.generateContent("gemini-1.5-flash", apiKeys[i], request)
                if (response.isSuccessful) {
                    val body = response.body()
                    return@withContext body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from AI."
                } else {
                    if (response.code() == 429 || response.code() == 503) {
                        continue // Try next key
                    } else {
                        return@withContext "AI Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                if (i == apiKeys.size - 1) return@withContext "Error reaching AI Advisor: ${e.message}"
                continue
            }
        }
        
        "All AI keys are currently unavailable."
    }
}
