package com.example.valora

import kotlin.math.pow

data class YearlyPoint(
    val year: Int,
    val totalInvested: Double,
    val nominalValue: Double,
    val realValue: Double
)

data class SimulationResult(
    val totalInvested: Double = 0.0,
    val nominalValue: Double = 0.0,
    val realValue: Double = 0.0,
    val history: List<YearlyPoint> = emptyList()
)

fun calculateSavings(
    initialCapital: Double,
    monthlyDeposit: Double,
    annualReturnRate: Double,
    annualInflationRate: Double,
    years: Int
): SimulationResult {
    val months = years * 12
    val monthlyReturn = (annualReturnRate / 100.0) / 12.0

    var currentNominal = initialCapital
    var totalInvested = initialCapital

    val history = mutableListOf<YearlyPoint>()
    history.add(YearlyPoint(0, initialCapital, initialCapital, initialCapital))

    for (m in 1..months) {
        currentNominal = currentNominal * (1 + monthlyReturn) + monthlyDeposit
        totalInvested += monthlyDeposit

        if (m % 12 == 0) {
            val currentYear = m / 12
            val inflationFactor = (1.0 + (annualInflationRate / 100.0)).pow(currentYear.toDouble())
            val realVal = if (inflationFactor > 0) currentNominal / inflationFactor else currentNominal

            history.add(
                YearlyPoint(
                    year = currentYear,
                    totalInvested = totalInvested,
                    nominalValue = currentNominal,
                    realValue = realVal
                )
            )
        }
    }

    val finalRealValue = history.lastOrNull()?.realValue ?: currentNominal

    return SimulationResult(
        totalInvested = totalInvested,
        nominalValue = currentNominal,
        realValue = finalRealValue,
        history = history
    )
}