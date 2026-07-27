package com.example.valora

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.pow

data class ChartPoint(
    val year: Int,
    val invested: Double,
    val nominal: Double,
    val real: Double
)

class ValoraViewModel : ViewModel() {

    private val repository = InflationRepository()

    // 1. ÉTATS DES CHAMPS DE SAISIE
    var initialCapital by mutableStateOf("10000")
        private set

    var monthlyContribution by mutableStateOf("200")
        private set

    var returnRate by mutableStateOf("7.0")
        private set

    var inflationRate by mutableStateOf("2.0")
        private set

    var years by mutableStateOf("20")
        private set

    // 2. ÉTATS DU CHARGEMENT API
    var isLoadingInflation by mutableStateOf(false)
        private set

    var inflationApiError by mutableStateOf<String?>(null)
        private set

    // 3. HANDLERS DE SAISIE
    fun onInitialCapitalChange(newValue: String) { initialCapital = newValue }
    fun onMonthlyContributionChange(newValue: String) { monthlyContribution = newValue }
    fun onReturnRateChange(newValue: String) { returnRate = newValue }
    fun onInflationRateChange(newValue: String) { inflationRate = newValue }
    fun onYearsChange(newValue: String) { years = newValue }

    // 4. SYNCHRONISATION BANQUE MONDIALE
    fun syncInflationFromApi(countryCode: String = "FRA") {
        viewModelScope.launch {
            isLoadingInflation = true
            inflationApiError = null

            val fetchedRate = repository.fetchLatestInflation(countryCode)
            if (fetchedRate != null) {
                val rounded = Math.round(fetchedRate * 10.0) / 10.0
                inflationRate = rounded.toString()
            } else {
                inflationApiError = "Erreur de connexion à la Banque Mondiale."
            }

            isLoadingInflation = false
        }
    }

    // 5. PARSAGE SÉCURISÉ
    private val parsedInitialCapital: Double get() = initialCapital.toDoubleOrNull() ?: 0.0
    private val parsedMonthlyContribution: Double get() = monthlyContribution.toDoubleOrNull() ?: 0.0
    private val parsedReturnRate: Double get() = (returnRate.toDoubleOrNull() ?: 0.0) / 100.0
    private val parsedInflationRate: Double get() = (inflationRate.toDoubleOrNull() ?: 0.0) / 100.0
    private val parsedYears: Int get() = years.toIntOrNull() ?: 0

    // 6. CALCULS FINANCIERS
    val totalInvested: Double
        get() = parsedInitialCapital + (parsedMonthlyContribution * 12 * parsedYears)

    val nominalValue: Double
        get() {
            val p = parsedInitialCapital
            val pm = parsedMonthlyContribution
            val r = parsedReturnRate / 12.0
            val n = parsedYears * 12

            if (n <= 0) return p
            if (r == 0.0) return p + (pm * n)

            val futureValueCapital = p * (1 + r).pow(n)
            val futureValueContributions = pm * (((1 + r).pow(n) - 1) / r)

            return futureValueCapital + futureValueContributions
        }

    val realValue: Double
        get() {
            val nominal = nominalValue
            val i = parsedInflationRate
            val n = parsedYears.toDouble()

            if (i == 0.0 || n <= 0) return nominal
            return nominal / (1 + i).pow(n)
        }

    // 7. GÉNÉRATION DES POINTS DU GRAPHIQUE
    val chartPoints: List<ChartPoint>
        get() {
            val p = parsedInitialCapital
            val pm = parsedMonthlyContribution
            val r = parsedReturnRate / 12.0
            val inf = parsedInflationRate
            val totalYears = parsedYears.coerceIn(1, 50)

            val points = mutableListOf<ChartPoint>()
            for (y in 0..totalYears) {
                val n = y * 12
                val inv = p + (pm * n)
                val nom = if (r == 0.0) inv else p * (1 + r).pow(n) + pm * (((1 + r).pow(n) - 1) / r)
                val real = if (inf == 0.0) nom else nom / (1 + inf).pow(y.toDouble())
                points.add(ChartPoint(year = y, invested = inv, nominal = nom, real = real))
            }
            return points
        }
}