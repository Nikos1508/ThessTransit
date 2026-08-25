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

        Log.d(TAG, "================================")
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
                Log.d(TAG, "Calling Nominatim...")
                val apiResults = NominatimClient.api.search(query = cleanQuery)

                Log.d(TAG, "Nominatim returned ${apiResults.size} results")

                val mappedResults = apiResults.mapNotNull { result ->

                    Log.d(TAG,
                        "RAW RESULT -> displayName=${result.displayName}, " +
                                "lat=${result.lat}, lon=${result.lon}"
                    )

                    val title = result.displayName
                    val latitude = result.lat?.toDoubleOrNull()
                    val longitude = result.lon?.toDoubleOrNull()

                    if (
                        title.isNullOrBlank() ||
                        latitude == null ||
                        longitude == null
                    ) {
                        Log.w(TAG, "Skipping invalid result: $result")
                        null
                    } else {
                        SearchResult(
                            title = title,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                }
                Log.d(TAG, "Mapped ${mappedResults.size} valid results")
                onResult(mappedResults)

            } catch (e: CancellationException) {
                Log.d(TAG, "Search cancelled: `$cleanQuery`")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Nominatim search FAILED for `$cleanQuery`", e)
                onResult(emptyList())
            }
        }
    }
}