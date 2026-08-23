package com.example.thesstransit.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.item.SearchResult
import com.example.thesstransit.ui.network.NominatimClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

class TransitSearchViewModel : ViewModel() {

    companion object {
        private const val TAG = "TransitSearchVM"
    }

    private var searchJob: Job? = null

    fun search(
        query: String,
        onResult: (List<SearchResult>) -> Unit
    ) {
        val cleanQuery = query.trim()
        Log.d(TAG, "Search requested: `$cleanQuery`")

        searchJob?.cancel()

        if (cleanQuery.length < 2) {
            Log.d(TAG, "Query too short")
            onResult(emptyList())
            return
        }

        searchJob = viewModelScope.launch {

            delay(400.milliseconds)

            try {
                Log.d(TAG, "Calling Nominatim for: '$cleanQuery'")

                val results = NominatimClient.api.search(query = cleanQuery)

                Log.d(TAG, "Nominatim returned ${results.size} results")

                val mappedResults = results.mapNotNull { result ->
                    try{
                        SearchResult(
                            title = result.displayName,
                            latitude = result.lat.toDouble(),
                            longitude = result.lon.toDouble()
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse result: $result", e)
                        null
                    }
                }
                Log.d(TAG, "Mapped ${mappedResults.size} valid results")
                onResult(mappedResults)
            } catch (e: CancellationException) {
                Log.d(TAG, "Search cancelled: `$cleanQuery`")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Nominatim search failed for `$cleanQuery`", e)
                onResult(emptyList())
            }
        }
    }
}