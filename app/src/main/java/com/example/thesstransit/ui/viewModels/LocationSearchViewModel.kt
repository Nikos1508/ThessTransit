package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.item.SearchResult
import com.example.thesstransit.ui.network.NominatimAddress
import com.example.thesstransit.ui.network.NominatimClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LocationSearchViewModel : ViewModel() {

    private var job: Job? = null

    private fun formatAddress(address: NominatimAddress?): String {
        if (address == null) {
            return ""
        }

        val road = buildString {
            address.road?.let {
                append(it)
            }

            address.houseNumber?.let {
                if (isNotBlank()) {
                    append(" ")
                }
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
            ?.replace(oldValue = "Περιφερειακή Ενότητα", newValue = "")
            ?.trim()

        return listOfNotNull(
            road.takeIf { it.isNotBlank() },
            locality?.takeIf { it.isNotBlank() },
            region?.takeIf { it.isNotBlank() }
        )
            .distinct()
            .joinToString(", ")
    }

    fun search(
        query: String,
        onResult: (List<SearchResult>) -> Unit
    ) {
        job?.cancel()
        val cleanQuery = query.trim()

        if (cleanQuery.length < 2) {
            onResult(emptyList())
            return
        }

        job = viewModelScope.launch {

            delay(500.milliseconds)

            try {

                val searchQuery =
                    if (cleanQuery.contains("Ελλάδα", ignoreCase = true)) {
                        cleanQuery
                    } else {
                        "$cleanQuery, Ελλάδα"
                    }

                val response = NominatimClient.api.search(query = searchQuery)

                val wantsNumber = cleanQuery.any { it.isDigit() }

                val results = response
                    .filter { result ->
                        if (wantsNumber) {
                            result.address?.houseNumber != null
                        } else {
                            true
                        }
                    }
                    .mapNotNull { result ->

                        val latitude = result.lat?.toDoubleOrNull()
                        val longitude = result.lon?.toDoubleOrNull()

                        if (latitude == null || longitude == null) {
                            return@mapNotNull null
                        }

                        val displayName = result.displayName ?: return@mapNotNull null

                        val title = formatAddress(result.address).ifBlank { displayName }

                        SearchResult(
                            title = title,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                    .distinctBy { it.title.lowercase() }
                    .sortedByDescending { it.title.contains(cleanQuery, ignoreCase = true) }

                onResult(results)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun reverse(
        lat: Double,
        lon: Double,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result =
                    NominatimClient.api.reverse(
                        lat = lat,
                        lon = lon
                    )

                val address = formatAddress(result.address)

                val finalName: String =
                    if (address.isNotBlank()) {
                        address
                    } else {
                        result.displayName ?: "$lat, $lon"
                    }

                onResult(finalName)
            } catch (e: Exception) {
                onResult("$lat, $lon")
            }
        }
    }
}