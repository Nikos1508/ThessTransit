package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.item.SearchResult
import com.example.thesstransit.ui.network.NominatimClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TransitSearchViewModel : ViewModel() {

    private var searchJob: Job? = null

    fun search(
        query: String,
        onResult: (List<SearchResult>) -> Unit
    ) {
        searchJob?.cancel()

        val cleanQuery = query.trim()

        if (cleanQuery.length < 2) {
            onResult(emptyList())
            return
        }

        searchJob = viewModelScope.launch {

            delay(400.milliseconds)

            try {
                val results = NominatimClient.api.search(query = cleanQuery)

                val mappedResults =
                    results.map {
                        SearchResult(
                            title = it.displayName,
                            latitude = it.lat.toDouble(),
                            longitude = it.lon.toDouble()
                        )
                    }
                onResult(mappedResults)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(emptyList())
            }
        }
    }
}