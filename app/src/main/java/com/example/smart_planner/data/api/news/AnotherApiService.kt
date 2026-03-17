package com.example.smart_planner.data.api.news

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class TestRequest(
    @SerializedName("test_field")
    val testField: String,
    @SerializedName("value")
    val value: Int
)

data class TestResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)

interface AnotherApiService {
    @Headers("Content-Type: application/json")
    @POST("anything") // httpbin.org/anything эхо-сервер для тестирования
    suspend fun sendTestData(
        @Body request: TestRequest
    ): TestResponse
}