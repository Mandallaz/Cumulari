package com.mandallaz.cumulari

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
fun CumulariChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxVal = points.maxOf { maxOf(it.nominal, it.real, it.invested) }.coerceAtLeast(1.0)

    Column(modifier = modifier) {
        // Zone de dessin du graphique
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            val pathNominal = Path()
            val pathReal = Path()
            val pathInvested = Path()

            points.forEachIndexed { index, point ->
                val x = index * stepX
                val yNominal = height - ((point.nominal / maxVal) * height).toFloat()
                val yReal = height - ((point.real / maxVal) * height).toFloat()
                val yInvested = height - ((point.invested / maxVal) * height).toFloat()

                if (index == 0) {
                    pathNominal.moveTo(x, yNominal)
                    pathReal.moveTo(x, yReal)
                    pathInvested.moveTo(x, yInvested)
                } else {
                    pathNominal.lineTo(x, yNominal)
                    pathReal.lineTo(x, yReal)
                    pathInvested.lineTo(x, yInvested)
                }
            }

            // Ligne Investi (Gris)
            drawPath(
                path = pathInvested,
                color = Color.Gray,
                style = Stroke(width = 3f)
            )

            // Ligne Valeur Nominale (Vert)
            drawPath(
                path = pathNominal,
                color = Color(0xFF4CAF50),
                style = Stroke(width = 5f)
            )

            // Ligne Valeur Réelle (Bleu)
            drawPath(
                path = pathReal,
                color = Color(0xFF2196F3),
                style = Stroke(width = 5f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Légende traduite dynamiquement via stringResource
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(
                color = Color(0xFF4CAF50),
                label = stringResource(R.string.chart_legend_nominal)
            )
            LegendItem(
                color = Color(0xFF2196F3),
                label = stringResource(R.string.chart_legend_real)
            )
            LegendItem(
                color = Color.Gray,
                label = stringResource(R.string.chart_legend_invested)
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}