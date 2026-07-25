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

        if (query.trim().length < 2) {
            onResult( emptyList() )
            return
        }

        searchJob = viewModelScope.launch {

            delay(500.milliseconds) //Μπορεί να γινει και delay(300)

            try {
                val results = NominatimClient.api.search( query = query )

                onResult(
                    results.map {
                        SearchResult(
                            title = it.display_name,

                            latitude = it.lat.toDouble(),

                            longitude = it.lon.toDouble()
                        )
                    }

                )
            } catch(e: Exception) {
                onResult(emptyList())
            }
        }
    }
}