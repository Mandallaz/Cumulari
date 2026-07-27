package com.example.valora

import com.google.gson.Gson

class InflationRepository {
    private val api = RetrofitClient.apiService

    suspend fun fetchLatestInflation(countryCode: String = "FRA"): Double? {
        return try {
            val response = api.getInflationData(countryCode = countryCode)
            if (response.size() > 1) {
                val recordsArray = response[1].asJsonArray
                val gson = Gson()

                for (element in recordsArray) {
                    val record = gson.fromJson(element, InflationRecord::class.java)
                    if (record.value != null) {
                        return record.value // Retourne ex: 4.9 %
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}