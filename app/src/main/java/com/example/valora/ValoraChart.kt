package com.example.valora // Ajustez le package selon votre projet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ValoraChart(
    history: List<YearlyPoint>,
    modifier: Modifier = Modifier
) {
    if (history.size < 2) return

    val nominalColor = MaterialTheme.colorScheme.primary
    val realColor = MaterialTheme.colorScheme.tertiary
    val investedColor = Color.Gray

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Légende du graphique basée sur les ressources strings.xml
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartLegendItem(
                    color = nominalColor,
                    label = stringResource(R.string.legend_nominal)
                )
                ChartLegendItem(
                    color = realColor,
                    label = stringResource(R.string.legend_real)
                )
                ChartLegendItem(
                    color = investedColor,
                    label = stringResource(R.string.legend_invested)
                )
            }

            // Zone de dessin du Graphique Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = size.width
                val height = size.height

                val maxVal = history.maxOfOrNull { maxOf(it.nominalValue, it.totalInvested) } ?: 1.0
                val minVal = 0.0

                val stepsX = history.size - 1

                fun getX(index: Int): Float = (index.toFloat() / stepsX) * width
                fun getY(value: Double): Float = height - (((value - minVal) / (maxVal - minVal)).toFloat() * height)

                val pathNominal = Path()
                val pathReal = Path()
                val pathInvested = Path()

                history.forEachIndexed { i, point ->
                    val x = getX(i)
                    val yNominal = getY(point.nominalValue)
                    val yReal = getY(point.realValue)
                    val yInvested = getY(point.totalInvested)

                    if (i == 0) {
                        pathNominal.moveTo(x, yNominal)
                        pathReal.moveTo(x, yReal)
                        pathInvested.moveTo(x, yInvested)
                    } else {
                        pathNominal.lineTo(x, yNominal)
                        pathReal.lineTo(x, yReal)
                        pathInvested.lineTo(x, yInvested)
                    }
                }

                // Tracé des 3 courbes
                drawPath(pathInvested, color = investedColor, style = Stroke(width = 3f))
                drawPath(pathReal, color = realColor, style = Stroke(width = 5f))
                drawPath(pathNominal, color = nominalColor, style = Stroke(width = 6f))
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}