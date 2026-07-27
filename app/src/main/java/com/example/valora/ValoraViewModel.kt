package com.example.valora

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.pow

data class Country(
    val code: String,
    val flagEmoji: String,
    @StringRes val nameRes: Int
)

data class ChartPoint(
    val year: Int,
    val invested: Double,
    val nominal: Double,
    val real: Double
)

class ValoraViewModel : ViewModel() {

    private val repository = InflationRepository()

    // 1. LISTE DES PAYS DISPONIBLES (Zone Euro en premier / par défaut)
    val availableCountries = listOf(
        Country(code = "EMU", flagEmoji = "🇪🇺", nameRes = R.string.country_eurozone),
        Country(code = "FRA", flagEmoji = "🇫🇷", nameRes = R.string.country_france),
        Country(code = "USA", flagEmoji = "🇺🇸", nameRes = R.string.country_usa),
        Country(code = "DEU", flagEmoji = "🇩🇪", nameRes = R.string.country_germany),
        Country(code = "ESP", flagEmoji = "🇪🇸", nameRes = R.string.country_spain),
        Country(code = "ITA", flagEmoji = "🇮🇹", nameRes = R.string.country_italy),
        Country(code = "GBR", flagEmoji = "🇬🇧", nameRes = R.string.country_uk),
        Country(code = "JPN", flagEmoji = "🇯🇵", nameRes = R.string.country_japan)
    )

    var selectedCountry by mutableStateOf(availableCountries.first())
        private set

    // 2. ÉTATS DES CHAMPS DE SAISIE
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

    // 3. ÉTATS API & ERREURS
    var isLoadingInflation by mutableStateOf(false)
        private set

    @get:StringRes
    var inflationApiErrorResId by mutableStateOf<Int?>(null)
        private set

    // 4. CHARGEMENT AUTOMATIQUE AU DÉMARRAGE
    init {
        syncInflationFromApi()
    }

    // 5. GESTIONNAIRES DE SAISIE
    fun onCountrySelected(country: Country) {
        if (selectedCountry != country) {
            selectedCountry = country
            // Déclenche automatiquement la mise à jour du taux d'inflation
            syncInflationFromApi()
        }
    }

    fun onInitialCapitalChange(newValue: String) { initialCapital = newValue }
    fun onMonthlyContributionChange(newValue: String) { monthlyContribution = newValue }
    fun onReturnRateChange(newValue: String) { returnRate = newValue }
    fun onInflationRateChange(newValue: String) { inflationRate = newValue }
    fun onYearsChange(newValue: String) { years = newValue }

    // 6. SYNCHRONISATION BANQUE MONDIALE
    fun syncInflationFromApi() {
        viewModelScope.launch {
            isLoadingInflation = true
            inflationApiErrorResId = null

            val fetchedRate = repository.fetchLatestInflation(selectedCountry.code)
            if (fetchedRate != null) {
                val rounded = Math.round(fetchedRate * 10.0) / 10.0
                inflationRate = rounded.toString()
            } else {
                inflationApiErrorResId = R.string.error_inflation_fetch
            }

            isLoadingInflation = false
        }
    }

    // 7. PARSAGE SÉCURISÉ DES VALEURS SAISIES
    private val parsedInitialCapital: Double get() = initialCapital.toDoubleOrNull() ?: 0.0
    private val parsedMonthlyContribution: Double get() = monthlyContribution.toDoubleOrNull() ?: 0.0
    private val parsedReturnRate: Double get() = (returnRate.toDoubleOrNull() ?: 0.0) / 100.0
    private val parsedInflationRate: Double get() = (inflationRate.toDoubleOrNull() ?: 0.0) / 100.0
    private val parsedYears: Int get() = years.toIntOrNull() ?: 0

    // 8. CALCULS FINANCIERS
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

    // 9. POINTS POUR LE GRAPHIQUE D'ÉVOLUTION
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