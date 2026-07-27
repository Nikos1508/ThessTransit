package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.item.SearchResult
import com.example.thesstransit.ui.network.NominatimAddress
import com.example.thesstransit.ui.network.NominatimClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LocationSearchViewModel: ViewModel() {
    private var job: Job? = null

    private fun formatAddress(address: NominatimAddress?): String {
        if (address == null)
            return ""

        val road = buildString {
            address.road?.let { append(it) }

            address.houseNumber?.let {
                if ( isNotBlank() )
                    append(" ")

                append(it)
            }
        }

        val locality =
            address.village
                ?: address.town
                ?: address.city
                ?: address.suburb
                ?: address.municipality

        val region = address.county
            ?.replace(
                oldValue = "Περιφερειακή Ενότητα",
                newValue = ""
            )
            ?.trim()

        return listOfNotNull(
            road.takeIf { it.isNotBlank() },
            locality,
            region
        )
            .distinct()
            .joinToString(", ")
    }

    fun search(
        query: String,
        onResult:(List<SearchResult>) -> Unit
    ) {

        job?.cancel()

        if (query.length < 2) {

            onResult( emptyList() )
            return

        }

        job = viewModelScope.launch {
            delay(500.milliseconds)

            runCatching {

                val response = NominatimClient.api.search(
                    query =
                        if (query.contains("Ελλάδα", true))
                            query
                        else
                            "$query, Ελλάδα"
                )

                val wantsNumber = query.any { it.isDigit() }

                val results = response
                    .filter {
                        if (wantsNumber)
                            it.address?.houseNumber != null
                        else
                            true
                    }
                    .map {
                        SearchResult(
                            title = formatAddress(it.address).ifBlank { it.displayName },
                            latitude = it.lat.toDouble(),
                            longitude = it.lon.toDouble()
                        )
                    }
                    .distinctBy { it.title.lowercase() }
                    .sortedByDescending {
                        it.title.contains(query, true)
                    }

                onResult(results)
            }.onFailure {
                onResult( emptyList() )
            }
        }
    }

    fun reverse(
        lat: Double,
        lon: Double,
        onResult: (String) -> Unit
    ) {

        viewModelScope.launch {
             runCatching {
                 val result =
                     NominatimClient.api.reverse(
                         lat,
                         lon
                     )

                 val address = formatAddress(result.address)

                 onResult(
                     address.ifBlank {
                         result.displayName
                             ?: "$lat, $lon"
                     }
                 )

             }.onFailure {
                 onResult(
                     "$lat , $lon"
                 )
             }
        }
    }
}