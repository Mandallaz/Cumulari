package com.mandallaz.cumulari

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

data class Country(
    val code: String,
    val flagEmoji: String,
    @param:StringRes val nameRes: Int,
    // Locale utilisée pour formater les montants (symbole + séparateurs) dans la devise du pays.
    val currencyLocale: Locale
)

data class ChartPoint(
    val year: Int,
    val invested: Double,
    val nominal: Double,
    val real: Double
)

class CumulariViewModel(
    private val repository: InflationRepository = InflationRepository()
) : ViewModel() {

    val availableCountries = listOf(
        Country(code = "EMU", flagEmoji = "🇪🇺", nameRes = R.string.country_eurozone, currencyLocale = Locale.FRANCE),
        Country(code = "FRA", flagEmoji = "🇫🇷", nameRes = R.string.country_france, currencyLocale = Locale.FRANCE),
        Country(code = "USA", flagEmoji = "🇺🇸", nameRes = R.string.country_usa, currencyLocale = Locale.US),
        Country(code = "DEU", flagEmoji = "🇩🇪", nameRes = R.string.country_germany, currencyLocale = Locale.GERMANY),
        Country(code = "ESP", flagEmoji = "🇪🇸", nameRes = R.string.country_spain, currencyLocale = Locale.Builder().setLanguage("es").setRegion("ES").build()),
        Country(code = "ITA", flagEmoji = "🇮🇹", nameRes = R.string.country_italy, currencyLocale = Locale.ITALY),
        Country(code = "GBR", flagEmoji = "🇬🇧", nameRes = R.string.country_uk, currencyLocale = Locale.UK),
        Country(code = "JPN", flagEmoji = "🇯🇵", nameRes = R.string.country_japan, currencyLocale = Locale.JAPAN)
    )

    var selectedCountry by mutableStateOf(availableCountries.first())
        private set

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

    var isLoadingInflation by mutableStateOf(false)
        private set

    @get:StringRes
    var inflationApiErrorResId by mutableStateOf<Int?>(null)
        private set

    private var syncJob: Job? = null

    init {
        syncInflationFromApi()
    }

    fun onCountrySelected(country: Country) {
        if (selectedCountry != country) {
            selectedCountry = country
            syncInflationFromApi()
        }
    }

    fun onInitialCapitalChange(newValue: String) { initialCapital = newValue }
    fun onMonthlyContributionChange(newValue: String) { monthlyContribution = newValue }
    fun onReturnRateChange(newValue: String) { returnRate = newValue }
    fun onInflationRateChange(newValue: String) { inflationRate = newValue }
    fun onYearsChange(newValue: String) { years = newValue }

    fun syncInflationFromApi() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            isLoadingInflation = true
            inflationApiErrorResId = null

            when (val result = repository.fetchLatestInflation(selectedCountry.code)) {
                is InflationFetchResult.Success -> {
                    val rounded = Math.round(result.rate * 10.0) / 10.0
                    inflationRate = rounded.toString()
                }
                is InflationFetchResult.Failure.NoInternet ->
                    inflationApiErrorResId = R.string.error_no_internet
                is InflationFetchResult.Failure.Timeout ->
                    inflationApiErrorResId = R.string.error_timeout
                is InflationFetchResult.Failure.ServerError ->
                    inflationApiErrorResId = R.string.error_server
                is InflationFetchResult.Failure.ParsingError ->
                    inflationApiErrorResId = R.string.error_parsing
                is InflationFetchResult.Failure.Unknown ->
                    inflationApiErrorResId = R.string.error_inflation_fetch
            }

            isLoadingInflation = false
        }
    }

    private val parsedInitialCapital: Double get() = initialCapital.toDoubleOrNull() ?: 0.0
    private val parsedMonthlyContribution: Double get() = monthlyContribution.toDoubleOrNull() ?: 0.0
    private val parsedReturnRate: Double get() = returnRate.toDoubleOrNull() ?: 0.0
    private val parsedInflationRate: Double get() = inflationRate.toDoubleOrNull() ?: 0.0
    private val parsedYears: Int get() = years.toIntOrNull() ?: 0

    private val simulation: SimulationResult
        get() = calculateSavings(
            initialCapital = parsedInitialCapital,
            monthlyDeposit = parsedMonthlyContribution,
            annualReturnRate = parsedReturnRate,
            annualInflationRate = parsedInflationRate,
            years = parsedYears
        )

    val totalInvested: Double get() = simulation.totalInvested
    val nominalValue: Double get() = simulation.nominalValue
    val realValue: Double get() = simulation.realValue

    val chartPoints: List<ChartPoint>
        get() = simulation.history.map {
            ChartPoint(
                year = it.year,
                invested = it.totalInvested,
                nominal = it.nominalValue,
                real = it.realValue
            )
        }
}