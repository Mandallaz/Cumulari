package com.example.valora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValoraScreen(
    viewModel: ValoraViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    // Le formateur suit la devise du pays sélectionné (ex: USD pour les USA, JPY pour le Japon)
    // au lieu d'afficher systématiquement des euros.
    val currencyFormatter = remember(viewModel.selectedCountry) {
        NumberFormat.getCurrencyInstance(viewModel.selectedCountry.currencyLocale)
    }
    var countryMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_title), fontWeight = FontWeight.Bold) },
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
                        text = stringResource(R.string.results_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ResultRow(
                        label = stringResource(R.string.label_total_invested),
                        value = currencyFormatter.format(viewModel.totalInvested)
                    )

                    ResultRow(
                        label = stringResource(R.string.label_nominal_value),
                        value = currencyFormatter.format(viewModel.nominalValue),
                        isBold = true
                    )

                    ResultRow(
                        label = stringResource(R.string.label_real_value),
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
                        text = stringResource(R.string.chart_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ValoraChart(points = viewModel.chartPoints)
                }
            }

            Text(
                text = stringResource(R.string.section_parameters),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 3. CAPITAL INITIAL ET VERSEMENT MENSUEL
            OutlinedTextField(
                value = viewModel.initialCapital,
                onValueChange = { viewModel.onInitialCapitalChange(it) },
                label = { Text(stringResource(R.string.label_initial_capital)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.monthlyContribution,
                onValueChange = { viewModel.onMonthlyContributionChange(it) },
                label = { Text(stringResource(R.string.label_monthly_contribution)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 4. RENDEMENT ANNUEL ESTIMÉ (TAUX D'INTÉRÊT)
            OutlinedTextField(
                value = viewModel.returnRate,
                onValueChange = { viewModel.onReturnRateChange(it) },
                label = { Text(stringResource(R.string.label_return_rate)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 5. SÉLECTEUR DE PAYS (Placé juste au-dessus du taux d'inflation)
            ExposedDropdownMenuBox(
                expanded = countryMenuExpanded,
                onExpandedChange = { countryMenuExpanded = !countryMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val currentCountryName = stringResource(viewModel.selectedCountry.nameRes)
                OutlinedTextField(
                    value = "${viewModel.selectedCountry.flagEmoji} $currentCountryName",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_country_selector)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = countryMenuExpanded,
                    onDismissRequest = { countryMenuExpanded = false }
                ) {
                    viewModel.availableCountries.forEach { country ->
                        val countryName = stringResource(country.nameRes)
                        DropdownMenuItem(
                            text = { Text("${country.flagEmoji} $countryName") },
                            onClick = {
                                viewModel.onCountrySelected(country)
                                countryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // 6. CHAMP INFLATION + BOUTON SYNC
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.inflationRate,
                    onValueChange = { viewModel.onInflationRateChange(it) },
                    label = { Text(stringResource(R.string.label_inflation_rate)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = viewModel.inflationApiErrorResId != null,
                    supportingText = {
                        viewModel.inflationApiErrorResId?.let { resId ->
                            Text(stringResource(resId))
                        }
                    }
                )

                IconButton(
                    onClick = { viewModel.syncInflationFromApi() },
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
                            contentDescription = stringResource(R.string.cd_sync_button)
                        )
                    }
                }
            }

            // 7. DURÉE DU PLACEMENT
            OutlinedTextField(
                value = viewModel.years,
                onValueChange = { viewModel.onYearsChange(it) },
                label = { Text(stringResource(R.string.label_years)) },
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
    color: Color = MaterialTheme.colorScheme.onSurface
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