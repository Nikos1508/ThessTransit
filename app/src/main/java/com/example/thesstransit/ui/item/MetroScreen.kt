package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.MetroBranch
import com.example.thesstransit.ui.viewModels.MetroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroScreen(
    onBackClick: () -> Unit
) {
    val vm: MetroViewModel = viewModel()

    ScreenHeader(
        title = "UNDER CONSTRUCTION",
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1700.dp)
                    .padding(24.dp)
            ) {
                MetroMapCanvas(
                    modifier = Modifier.fillMaxSize()
                )

                MetroTrain(vm)

                MetroStationsOverlay(vm = vm)
            }

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

        val centerX = size.width * 0.45f
        val splitY = size.height * 0.73f

        val top = 40f

        val leftX = centerX - 70f
        val rightX = centerX + 70f

        val bottom = size.height - 40f

        val lineWidth = 20f

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
            end = Offset(rightX, bottom - 400f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

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
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (highlighted)
                    Color(0xFFFFD54F)
                else
                    MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 22.dp,
                vertical = 18.dp
            )
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class StationPosition(
    val x: Float,
    val y: Float
)

private fun stationPosition(
    index: Int,
    branch: MetroBranch
): StationPosition {
    val firstY = 0.06f
    val spacing = 0.075f

    return when (branch) {

        MetroBranch.MAIN ->
            StationPosition(
                x = 0.57f,
                y = firstY + spacing * index
            )

        MetroBranch.MIKRA ->
            StationPosition(
                x = 0.72f,
                y = 0.86f + spacing * (index - 11)
            )

        MetroBranch.NEA_ELVETIA ->
            StationPosition(
                x = 0.28f,
                y = 0.86f + spacing * (index - 16)
            )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MetroStationsOverlay(
    vm: MetroViewModel
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = maxWidth
        val height = maxHeight

        vm.stations.forEachIndexed { index, station ->

            val pos = stationPosition(
                index,
                station.branch
            )

            MetroStationCard(
                title = station.mainName,
                subtitle = station.secName,
                highlighted = index == 3,
                modifier = Modifier.offset {
                    IntOffset(
                        x = (pos.x * width.toPx()).toInt(),
                        y = (pos.y * height.toPx()).toInt()
                    )
                }
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MetroTrain(
    vm: MetroViewModel
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = maxWidth
        val height = maxHeight

        val station =
            stationPosition(
                vm.currentStationIndex,
                vm.stations[vm.currentStationIndex].branch
            )

        Card(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (station.x * width.toPx()).toInt() - 20,
                        (station.y * height.toPx()).toInt() - 20
                    )
                },
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                Icons.Default.Train,
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}