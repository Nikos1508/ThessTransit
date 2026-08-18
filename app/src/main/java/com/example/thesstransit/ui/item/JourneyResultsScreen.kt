package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.network.JourneyLeg
import com.example.thesstransit.ui.network.JourneyOption
import com.example.thesstransit.ui.viewModels.JourneySearchViewModel
import com.example.thesstransit.ui.viewModels.JourneySearchViewModelFactory
import com.example.thesstransit.ui.network.JourneyRepository
import com.example.thesstransit.ui.viewModels.JourneySearchState

@Composable
fun JourneyResultsScreen(
    originLat: Double,
    originLon: Double,
    destLat: Double,
    destLon: Double,
    departTime: String,
    onBackClick: () -> Unit
) {

    val repository = androidx.compose.runtime.remember {
        JourneyRepository()
    }

    val viewModel: JourneySearchViewModel =
        viewModel(
            factory = JourneySearchViewModelFactory(
                repository
            )
        )

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(
        originLat,
        originLon,
        destLat,
        destLon,
        departTime
    ) {
        viewModel.search(
            originLat = originLat,
            originLon = originLon,
            destLat = destLat,
            destLon = destLon,
            departTime = departTime
        )
    }

    when (val current = state) {
        JourneySearchState.Idle -> Unit

        JourneySearchState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Υπολογισμός διαδρομής...")
            }
        }

        is JourneySearchState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    current.message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is JourneySearchState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    current.options
                ) { option ->

                    JourneyCard(option)
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(
    option: JourneyOption
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${option.depart} -> ${option.arrival}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${option.durationMinutes} min",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer( modifier = Modifier.height(4.dp) )

            Text(
                text = "${option.numTransfers} αλλαγές · " +
                        "${option.totalWalkSeconds / 60} λεπτά περπάτημα",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            option.reliabilityNote?.let {
                Spacer( modifier = Modifier.height(4.dp) )

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer( modifier = Modifier.height(12.dp) )

            option.legs.forEach { leg ->
                LegRow(leg)
            }
        }
    }
}

@Composable
private fun LegRow(
    leg: JourneyLeg
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (leg.mode) {
            "transit" -> {
                Text(
                    text = leg.routeShortName ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(48.dp)
                )

                Text(
                    text = "${leg.boardStopName} ->" +
                            leg.alightStopName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            "walk" -> {
                Text(
                    text = "Περπάτημα",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(80.dp)
                )

                Text(
                    text = "${leg.fromStopName} -> " +
                            "${leg.toStopName} " +
                            "(${leg.walkSeconds ?: 0} sec)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text(
            text = leg.arrival,
            style = MaterialTheme.typography.labelMedium
        )
    }
}