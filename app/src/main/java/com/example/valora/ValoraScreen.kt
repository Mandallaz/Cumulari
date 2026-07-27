package com.example.valora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValoraScreen(viewModel: ValoraViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ResultCard(result = state.result)

            ValoraChart(history = state.result.history)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            OutlinedTextField(
                value = state.initialCapital,
                onValueChange = viewModel::onInitialCapitalChange,
                label = { Text(stringResource(R.string.initial_capital)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.monthlyDeposit,
                onValueChange = viewModel::onMonthlyDepositChange,
                label = { Text(stringResource(R.string.monthly_deposit)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(stringResource(R.string.return_rate, state.returnRate))
            Slider(
                value = state.returnRate,
                onValueChange = viewModel::onReturnRateChange,
                valueRange = 0f..12f,
                steps = 23
            )

            Text(stringResource(R.string.inflation_rate, state.inflationRate))
            Slider(
                value = state.inflationRate,
                onValueChange = viewModel::onInflationRateChange,
                valueRange = 0f..10f,
                steps = 19
            )

            Text(stringResource(R.string.duration, state.years.toInt()))
            Slider(
                value = state.years,
                onValueChange = viewModel::onYearsChange,
                valueRange = 1f..40f,
                steps = 38
            )
        }
    }
}

@Composable
fun ResultCard(result: SimulationResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.real_value),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = String.format(Locale.US, "%.2f €", result.realValue),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.total_invested))
                Text(String.format(Locale.US, "%.2f €", result.totalInvested), fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.nominal_value))
                Text(String.format(Locale.US, "%.2f €", result.nominalValue), fontWeight = FontWeight.Bold)
            }
        }
    }
}