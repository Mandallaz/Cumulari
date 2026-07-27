package com.example.valora

import com.google.gson.JsonArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class InflationRecord(
    val date: String,    // Ex: "2023"
    val value: Double?   // Ex: 4.9 (null si pas encore publié)
)

interface WorldBankApiService {
    @GET("v2/country/{countryCode}/indicator/FP.CPI.TOTL.ZG")
    suspend fun getInflationData(
        @Path("countryCode") countryCode: String = "FRA",
        @Query("format") format: String = "json",
        @Query("per_page") perPage: Int = 5
    ): JsonArray
}

object RetrofitClient {
    private const val BASE_URL = "https://api.worldbank.org/"

    val apiService: WorldBankApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WorldBankApiService::class.java)
    }
}