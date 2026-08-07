package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.data.MetroBranch
import com.example.thesstransit.ui.data.MetroStop
import com.example.thesstransit.ui.viewModels.MetroViewModel
import kotlin.times

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
            shape = RoundedCornerShape(20.dp),
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
                    .height(2500.dp)
                    .padding(24.dp)
            ) {
                MetroMapCanvas( modifier = Modifier.fillMaxSize() )

                MetroStationsOverlay(vm)
                MetroTrain(vm)
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
        val splitY = size.height * 0.62f

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
            end = Offset(leftX, bottom + 650f),
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
            end = Offset(rightX, bottom - 900f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

    }
}

@Composable
private fun MetroStationCard(
    station: MetroStop,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    alignLeft: Boolean = false
) {

    Card(
        modifier = modifier
            .width(165.dp)
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.cardColors(
            containerColor =
                if (highlighted)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation( 6.dp )
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment =
                if (alignLeft)
                    Alignment.End
                else
                    Alignment.Start,
            modifier = Modifier.padding(
                horizontal = 16.dp
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.height(10.dp),
                        tint =
                            when (station.branch) {
                                MetroBranch.MAIN ->
                                    Color(0xFFD32F2F)

                                MetroBranch.MIKRA ->
                                    Color(0xFFD32F2F)

                                MetroBranch.NEA_ELVETIA ->
                                    Color(0xFF3949AB)
                            }
                    )

                    Spacer( modifier = Modifier.width(6.dp) )

                    Text(
                        when (station.branch) {
                            MetroBranch.MAIN ->
                                "MAIN"

                            MetroBranch.MIKRA ->
                                "MIKRA"

                            MetroBranch.NEA_ELVETIA ->
                                "NEA ELVETIA"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (highlighted) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }

                }

            }

            Text(
                text = station.mainName,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = station.secName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer( modifier = Modifier.height(2.dp) )

            HorizontalDivider()

            Spacer( modifier = Modifier.height(8.dp) )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.AccessTime,
                        title = "Next",
                        value = station.travelToNext?.let { "$it sec" } ?: "--"
                    )

                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        title = "Railway",
                        value = station.toRailwayStation?.let { "${it/60} min" } ?: "--"
                    )

                    station.toMikra?.let {
                        InfoRow(
                            icon = Icons.Default.South,
                            title = "Μίκρα",
                            value = "${it/60} min"
                        )
                    }

                    station.toNeaElvetia?.let {
                        InfoRow(
                            icon = Icons.Default.North,
                            title = "Νέα Ελβετία",
                            value = "${it/60} min"
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.height(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer( modifier = Modifier.width(6.dp) )

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
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
    val spacing = 0.105f

    return when (branch) {

        MetroBranch.MAIN ->
            StationPosition(
                x = 0.57f,
                y = firstY + spacing * index
            )

        MetroBranch.MIKRA ->
            StationPosition(
                x = 0.80f,
                y = 0.86f + spacing * (index - 11)
            )

        MetroBranch.NEA_ELVETIA ->
            StationPosition(
                x = 0.03f,
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

            val shift =
                when (station.branch) {
                    MetroBranch.MAIN ->
                        if (index % 2 == 0) -60 else 60

                    MetroBranch.MIKRA -> 60
                    MetroBranch.NEA_ELVETIA -> -60
                }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (pos.x * width.toPx()).toInt() - 9,
                            (pos.y * height.toPx()).toInt() + 36
                        )
                    }
                    .size(18.dp)
                    .background(
                        Color.White,
                        CircleShape
                    )
                    .border(
                        width = 4.dp,
                        color = if (station.branch == MetroBranch.MIKRA)
                            Color.Red
                        else
                            Color(0xFF283593),
                        CircleShape
                    )
            )

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(
                        pos.x * size.width,
                        pos.y * size.height + 45f
                        ),
                    end = Offset(
                        (pos.x * size.width) + shift,
                        pos.y * size.height + 45f
                    ),
                    strokeWidth = 4f
                )
            }

            MetroStationCard(
                station = station,
                highlighted = index == 3,
                alignLeft = station.branch == MetroBranch.NEA_ELVETIA,
                modifier = Modifier.offset {
                    val x =
                        if (station.branch == MetroBranch.NEA_ELVETIA)
                            (pos.x * width.toPx()).toInt() - 200
                        else
                            (pos.x * width.toPx()).toInt()

                    IntOffset(
                        x = x + shift,
                        y = (pos.y * height.toPx()).toInt()
                    )
                }
                    .shadow(
                        18.dp,
                        CircleShape,
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
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
                        x = (station.x * width.toPx()).toInt() + 18,
                        y = (station.y * height.toPx()).toInt() + 22
                    )
                },
            elevation = CardDefaults.cardElevation(12.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Train,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}