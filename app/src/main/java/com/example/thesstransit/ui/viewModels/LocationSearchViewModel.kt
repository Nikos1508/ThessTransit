package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.item.SearchResult
import com.example.thesstransit.ui.network.NominatimClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationSearchViewModel: ViewModel() {
    private var job: Job? = null

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
            delay(500)

            runCatching {

                val response = NominatimClient.api.search(query)

                val results =
                    response.map {

                        SearchResult(
                            title = it.display_name,
                            latitude = it.lat.toDouble(),
                            longitude = it.lon.toDouble()
                        )

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

                 onResult(
                     result.display_name ?:
                     "$lat , $lon"
                 )

             }.onFailure {
                 onResult(
                     "$lat , $lon"
                 )
             }
        }
    }
}