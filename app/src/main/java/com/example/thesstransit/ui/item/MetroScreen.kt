    package com.example.thesstransit.ui.item

    import androidx.compose.foundation.Canvas
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
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
                        .height(1250.dp)
                        .padding(24.dp)
                )
            }
        }
    }

    @Composable
    private fun MetroMapCanvas(
        modifier: Modifier = Modifier
    ) {

        Canvas(modifier = modifier) {

            val blue = Color(0xFF283593)
            val red = Color(0xFFD32F2F)

            val centerX = size.width * 0.45f

            val top = 40f

            val splitY = size.height * 0.73f

            val leftX = centerX - 70f
            val rightX = centerX + 70f

            val bottom = size.height - 40f

            val lineWidth = 16f

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
        }
    }