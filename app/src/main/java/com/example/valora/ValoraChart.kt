package com.example.valora

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ValoraChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return

    val maxVal = (points.maxOfOrNull { it.nominal } ?: 1.0) * 1.05

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height

            val pathNominal = Path()
            val pathReal = Path()
            val pathInvested = Path()

            points.forEachIndexed { index, pt ->
                val x = (index.toFloat() / (points.size - 1)) * width
                val yNominal = height - ((pt.nominal / maxVal).toFloat() * height)
                val yReal = height - ((pt.real / maxVal).toFloat() * height)
                val yInvested = height - ((pt.invested / maxVal).toFloat() * height)

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

            drawPath(path = pathInvested, color = Color.Gray, style = Stroke(width = 3f))
            drawPath(path = pathReal, color = Color(0xFF2196F3), style = Stroke(width = 5f))
            drawPath(path = pathNominal, color = Color(0xFF4CAF50), style = Stroke(width = 5f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color(0xFF4CAF50), label = "Nominal")
            LegendItem(color = Color(0xFF2196F3), label = "Pouvoir d'achat")
            LegendItem(color = Color.Gray, label = "Versé")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}