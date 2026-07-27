package com.example.valora

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ValoraUiState(
    val initialCapital: String = "1000",
    val monthlyDeposit: String = "150",
    val returnRate: Float = 5.0f,
    val inflationRate: Float = 2.5f,
    val years: Float = 10f
) {
    val result: SimulationResult
        get() = calculateSavings(
            initialCapital = initialCapital.toDoubleOrNull() ?: 0.0,
            monthlyDeposit = monthlyDeposit.toDoubleOrNull() ?: 0.0,
            annualReturnRate = returnRate.toDouble(),
            annualInflationRate = inflationRate.toDouble(),
            years = years.toInt()
        )
}

class ValoraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ValoraUiState())
    val uiState: StateFlow<ValoraUiState> = _uiState.asStateFlow()

    fun onInitialCapitalChange(value: String) { _uiState.value = _uiState.value.copy(initialCapital = value) }
    fun onMonthlyDepositChange(value: String) { _uiState.value = _uiState.value.copy(monthlyDeposit = value) }
    fun onReturnRateChange(value: Float) { _uiState.value = _uiState.value.copy(returnRate = value) }
    fun onInflationRateChange(value: Float) { _uiState.value = _uiState.value.copy(inflationRate = value) }
    fun onYearsChange(value: Float) { _uiState.value = _uiState.value.copy(years = value) }
}