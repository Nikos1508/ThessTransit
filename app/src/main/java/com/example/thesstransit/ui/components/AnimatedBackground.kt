package com.example.thesstransit.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedBackground() {

    val transition = rememberInfiniteTransition(label = "dots")

    val slowOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 9000,
                easing = LinearEasing
            ),
            RepeatMode.Restart
        ),
        label = "slow"
    )

    val fastOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "fast"
    )

    val baseColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawDots(
            offset = slowOffset,
            spacing = 55f,
            radius = 3f,
            color = baseColor.copy(alpha = 0.10f)
        )

        drawDots(
            offset = fastOffset,
            spacing = 90f,
            radius = 5f,
            color = Color.White.copy(alpha = 0.035f)
        )
    }
}


private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDots(
    offset: Float,
    spacing: Float,
    radius: Float,
    color: Color
) {
    var row = 0


    var y = -spacing


    while (y < size.height + spacing) {


        var x = if (row % 2 == 0)
            -spacing
        else
            -spacing / 2

        while (x < size.width + spacing) {
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(
                    x + offset,
                    y - offset
                )
            )

            x += spacing
        }

        y += spacing / 2

        row++
    }
}