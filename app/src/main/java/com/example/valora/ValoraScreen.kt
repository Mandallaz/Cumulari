package com.example.valora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValoraScreen(
    viewModel: ValoraViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Valora — Intérêts & Inflation", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. CARTE DES RÉSULTATS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Résultats de la simulation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ResultRow(
                        label = "Total investi de votre poche :",
                        value = currencyFormatter.format(viewModel.totalInvested)
                    )

                    ResultRow(
                        label = "Valeur nominale (brute) :",
                        value = currencyFormatter.format(viewModel.nominalValue),
                        isBold = true
                    )

                    ResultRow(
                        label = "Pouvoir d'achat réel (ajusté) :",
                        value = currencyFormatter.format(viewModel.realValue),
                        isBold = true,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. GRAPHIQUE D'ÉVOLUTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Évolution du capital",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ValoraChart(points = viewModel.chartPoints)
                }
            }

            Text(
                text = "Paramètres du placement",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 3. CHAMPS DE SAISIE
            OutlinedTextField(
                value = viewModel.initialCapital,
                onValueChange = { viewModel.onInitialCapitalChange(it) },
                label = { Text("Capital initial (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.monthlyContribution,
                onValueChange = { viewModel.onMonthlyContributionChange(it) },
                label = { Text("Versement mensuel (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.returnRate,
                onValueChange = { viewModel.onReturnRateChange(it) },
                label = { Text("Rendement annuel estimé (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.inflationRate,
                    onValueChange = { viewModel.onInflationRateChange(it) },
                    label = { Text("Taux d'inflation annuel (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = viewModel.inflationApiError != null,
                    supportingText = {
                        viewModel.inflationApiError?.let { Text(it) }
                    }
                )

                IconButton(
                    onClick = { viewModel.syncInflationFromApi("FRA") },
                    enabled = !viewModel.isLoadingInflation,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    if (viewModel.isLoadingInflation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Synchroniser avec la Banque Mondiale"
                        )
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.years,
                onValueChange = { viewModel.onYearsChange(it) },
                label = { Text("Durée du placement (années)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}