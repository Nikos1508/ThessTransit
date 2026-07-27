package com.example.thesstransit.ui.item

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.thesstransit.ui.components.ScreenHeader
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint
import android.text.Highlights
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroScreen(
    onBackClick: () -> Unit
) {
    ScreenHeader(
        title = "Μετρό Θεσσαλονίκης",
        onBackClick = onBackClick,
        onProfileClick = onBackClick
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Spacer( modifier = Modifier.height(72.dp) )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            MetroMapCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1650.dp)
                    .padding(24.dp)
            )
        }

        Spacer( modifier = Modifier.height(72.dp) )
    }
}

@Composable
private fun MetroMapCanvas(
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {

        val blue = Color(0xFF283593)
        val red = Color(0xFFD32F2F)

        val stationPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 34f
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val subtitlePaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 24f
            isAntiAlias = true
        }

        val firstY = 40f
        val spacing = 140f

        val centerX = size.width * 0.45f
        val splitY = size.height * 0.73f

        val top = 40f

        val leftX = centerX - 70f
        val rightX = centerX + 70f

        val bottom = size.height - 40f

        val lineWidth = 16f

        fun drawStation(
            y: Float,
            mainLang: String,
            secLang: String,
            highlighted: Boolean = false
        ) {
            drawCircle(
                color = Color.White,
                radius = 19f,
                center = Offset(centerX, y),
                style = Fill
            )

            drawCircle(
                color = Color.Black,
                radius = 19f,
                center = Offset(centerX, y),
                style = Stroke(width = 4.5f)
            )

            if (highlighted) {
                drawRoundRect(
                    color = Color(0xFFF9C92B),
                    topLeft = Offset(centerX + 45f, y - 34f),
                    size = androidx.compose.ui.geometry.Size(250f, 70f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                mainLang,
                centerX + 65f,
                y - 5f,
                subtitlePaint
            )

            drawContext.canvas.nativeCanvas.drawText(
                secLang,
                centerX + 65f,
                y + 26f,
                subtitlePaint
            )

        }

        drawLine(
            color = blue,
            start = Offset(centerX - 5f, top),
            end = Offset(centerX - 5f, splitY),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = red,
            start = Offset(centerX + 5f, top),
            end = Offset(centerX + 5f, splitY),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = blue,
            start = Offset(centerX - 5f, splitY),
            end = Offset(leftX, splitY + 55f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = blue,
            start = Offset(leftX, splitY + 55f),
            end = Offset(leftX, bottom),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = red,
            start = Offset(centerX + 5f, splitY),
            end = Offset(rightX, splitY + 55f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = red,
            start = Offset(rightX, splitY + 55f),
            end = Offset(rightX, bottom - 140f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawStation(y = firstY,               mainLang = "ΝΣ Σταθμός",   secLang = "New Railway Station")
        drawStation(y = firstY + spacing,     mainLang = "Δημοκρατίας",  secLang = "Dimokratias")
        drawStation(y = firstY + spacing * 2, mainLang = "Βενιζέλου",    secLang = "Venizelou")
        drawStation(y = firstY + spacing * 3, mainLang = "Αγίας Σοφίας", secLang = "Agias Sofias", true)
        drawStation(y = firstY + spacing * 4, mainLang = "Σιντριβάνι",   secLang = "Sintrivani")
        drawStation(y = firstY + spacing * 5, mainLang = "Πανεπιστήμιο", secLang = "Panepistimio")
        drawStation(y = firstY + spacing * 6, mainLang = "Παπάφη",       secLang = "Papafi")
        drawStation(y = firstY + spacing * 7, mainLang = "Ευκλείδης",    secLang = "Efkleidis")
        drawStation(y = firstY + spacing * 8, mainLang = "Φλέμινγκ",     secLang = "Fleming")
        drawStation(y = firstY + spacing * 9, mainLang = "Ανάληψη",      secLang = "Analipsi")
        drawStation(y = firstY + spacing * 10, mainLang = "25ης Μαρτίου",secLang = "25 Martiou")

    }
}

@Composable
private fun MetroStationCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (highlighted)
                    Color(0xFFFFD54F)
                else
                    MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp
            )
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}