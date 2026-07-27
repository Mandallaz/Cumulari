package com.example.valora

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// Représente les différentes issues possibles de l'appel réseau, pour que
// l'UI puisse afficher un message adapté (au lieu d'un message générique
// "erreur" quelle que soit la cause).
sealed class InflationFetchResult {
    data class Success(val rate: Double) : InflationFetchResult()
    sealed class Failure : InflationFetchResult() {
        object NoInternet : Failure()
        object Timeout : Failure()
        data class ServerError(val code: Int) : Failure()
        object ParsingError : Failure()
        data class Unknown(val message: String?) : Failure()
    }
}

class InflationRepository {
    private val api = RetrofitClient.apiService

    suspend fun fetchLatestInflation(countryCode: String = "FRA"): InflationFetchResult {
        return try {
            val response = api.getInflationData(countryCode = countryCode)

            if (response.size() <= 1) {
                return InflationFetchResult.Failure.ParsingError
            }

            val recordsArray = response[1].asJsonArray
            val gson = Gson()

            for (element in recordsArray) {
                val record = gson.fromJson(element, InflationRecord::class.java)
                if (record.value != null) {
                    return InflationFetchResult.Success(record.value)
                }
            }

            // Réponse valide mais sans donnée exploitable (aucune année récente publiée)
            InflationFetchResult.Failure.ParsingError

        } catch (_: UnknownHostException) {
            // Pas de connexion réseau (DNS injoignable)
            InflationFetchResult.Failure.NoInternet
        } catch (_: SocketTimeoutException) {
            // Le serveur n'a pas répondu à temps
            InflationFetchResult.Failure.Timeout
        } catch (e: HttpException) {
            // Le serveur a répondu mais avec un code d'erreur HTTP (4xx/5xx)
            InflationFetchResult.Failure.ServerError(e.code())
        } catch (_: JsonSyntaxException) {
            // Réponse reçue mais mal formée / inattendue
            InflationFetchResult.Failure.ParsingError
        } catch (_: IOException) {
            // Autre coupure réseau (ex: connexion perdue en cours de requête)
            InflationFetchResult.Failure.NoInternet
        } catch (e: Exception) {
            e.printStackTrace()
            InflationFetchResult.Failure.Unknown(e.message)
        }
    }
}