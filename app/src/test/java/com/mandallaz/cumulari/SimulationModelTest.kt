package com.mandallaz.cumulari

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationModelTest {

    private val delta = 1e-6

    @Test
    fun `zero years returns only the initial point`() {
        val result = calculateSavings(
            initialCapital = 1000.0,
            monthlyDeposit = 200.0,
            annualReturnRate = 7.0,
            annualInflationRate = 2.0,
            years = 0
        )

        assertEquals(1, result.history.size)
        assertEquals(0, result.history.first().year)
        assertEquals(1000.0, result.totalInvested, delta)
        assertEquals(1000.0, result.nominalValue, delta)
        assertEquals(1000.0, result.realValue, delta)
    }

    @Test
    fun `no deposit no return no inflation keeps capital flat`() {
        val result = calculateSavings(
            initialCapital = 1000.0,
            monthlyDeposit = 0.0,
            annualReturnRate = 0.0,
            annualInflationRate = 0.0,
            years = 5
        )

        assertEquals(1000.0, result.totalInvested, delta)
        assertEquals(1000.0, result.nominalValue, delta)
        assertEquals(1000.0, result.realValue, delta)
        assertEquals(6, result.history.size)
    }

    @Test
    fun `compound interest applies monthly with no deposit`() {
        val result = calculateSavings(
            initialCapital = 1000.0,
            monthlyDeposit = 0.0,
            annualReturnRate = 12.0,
            annualInflationRate = 0.0,
            years = 1
        )

        val expectedNominal = 1000.0 * Math.pow(1 + 0.12 / 12.0, 12.0)

        assertEquals(1000.0, result.totalInvested, delta)
        assertEquals(expectedNominal, result.nominalValue, delta)
        assertEquals(expectedNominal, result.realValue, delta)
    }

    @Test
    fun `monthly deposits accumulate without growth or inflation`() {
        val result = calculateSavings(
            initialCapital = 0.0,
            monthlyDeposit = 100.0,
            annualReturnRate = 0.0,
            annualInflationRate = 0.0,
            years = 1
        )

        assertEquals(1200.0, result.totalInvested, delta)
        assertEquals(1200.0, result.nominalValue, delta)
        assertEquals(1200.0, result.realValue, delta)
    }

    @Test
    fun `inflation reduces real value while nominal stays constant`() {
        val result = calculateSavings(
            initialCapital = 1000.0,
            monthlyDeposit = 0.0,
            annualReturnRate = 0.0,
            annualInflationRate = 10.0,
            years = 2
        )

        assertEquals(1000.0, result.totalInvested, delta)
        assertEquals(1000.0, result.nominalValue, delta)
        assertEquals(1000.0 / Math.pow(1.10, 2.0), result.realValue, delta)

        val yearOne = result.history.first { it.year == 1 }
        assertEquals(1000.0 / 1.10, yearOne.realValue, delta)
    }

    @Test
    fun `history contains one point per year in increasing order`() {
        val result = calculateSavings(
            initialCapital = 500.0,
            monthlyDeposit = 50.0,
            annualReturnRate = 5.0,
            annualInflationRate = 3.0,
            years = 10
        )

        assertEquals(11, result.history.size)
        assertEquals((0..10).toList(), result.history.map { it.year })
    }

    @Test
    fun `total invested always equals initial capital plus all deposits`() {
        val years = 15
        val monthlyDeposit = 150.0
        val initialCapital = 2000.0

        val result = calculateSavings(
            initialCapital = initialCapital,
            monthlyDeposit = monthlyDeposit,
            annualReturnRate = 4.0,
            annualInflationRate = 1.5,
            years = years
        )

        val expectedTotalInvested = initialCapital + monthlyDeposit * years * 12
        assertEquals(expectedTotalInvested, result.totalInvested, delta)
    }

    @Test
    fun `positive return keeps nominal value above total invested`() {
        val result = calculateSavings(
            initialCapital = 1000.0,
            monthlyDeposit = 100.0,
            annualReturnRate = 7.0,
            annualInflationRate = 2.0,
            years = 20
        )

        assertTrue(result.nominalValue > result.totalInvested)
        assertTrue(result.realValue < result.nominalValue)
    }
}
