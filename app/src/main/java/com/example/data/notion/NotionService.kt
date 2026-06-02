package com.example.data.notion

import com.squareup.moshi.JsonClass
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class NotionQueryRequest(
    val filter: Map<String, Any>? = null,
    val sorts: List<Map<String, String>>? = null
)

@JsonClass(generateAdapter = true)
data class NotionQueryResponse(
    val results: List<NotionPage>
)

@JsonClass(generateAdapter = true)
data class NotionPage(
    val id: String,
    val properties: Map<String, Any>
)

interface NotionApiService {
    @POST("v1/databases/{database_id}/query")
    suspend fun queryDatabase(
        @Path("database_id") databaseId: String,
        @Body request: NotionQueryRequest = NotionQueryRequest()
    ): NotionQueryResponse
    
    @POST("v1/pages")
    suspend fun createPage(
        @Body request: Map<String, Any>
    ): NotionPage
}

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Notion-Version", "2022-06-28")
            .build()
        return chain.proceed(request)
    }
}

object NotionApiClient {
    private const val BASE_URL = "https://api.notion.com/"

    fun createService(token: String): NotionApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
            
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            
        return retrofit.create(NotionApiService::class.java)
    }
}

class NotionService(private val token: String) {
    private val api = NotionApiClient.createService(token)
    
    suspend fun fetchTransactions(databaseId: String): List<NotionPage> {
        val response = api.queryDatabase(databaseId)
        return response.results
    }
    
    // Future sync methods will be hooked up to FinanceRepository here
}
