package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.recentSearchDataStore by preferencesDataStore(
    name = "recent_searches"
)

@Serializable
data class RecentSearch(
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val timesOpened: Int,
    val lastUsed: Long
)

class RecentSearchStorage(
    private val context: Context
) {

    companion object {
        private val SEARCHES_KEY = stringPreferencesKey("searches")
        private const val MAX_ITEMS = 15
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val searches: Flow<List<RecentSearch>> =
        context.recentSearchDataStore.data.map { prefs ->

            val raw = prefs[SEARCHES_KEY] ?: return@map emptyList()

            try {
                json.decodeFromString<List<RecentSearch>>(raw)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun saveSearch(
        title: String,
        latitude: Double,
        longitude: Double
    ) {
        context.recentSearchDataStore.edit { prefs ->

            val current =
                try {
                    json.decodeFromString<List<RecentSearch>>(
                        prefs[SEARCHES_KEY] ?: "[]"
                    )
                } catch (e: Exception) {
                    emptyList()
                }

            val now = System.currentTimeMillis()

            val existing = current.find {
                it.title == title
            }

            val updated =
                if (existing != null) {
                    current.map {
                        if (it.title == title) {
                            it.copy(
                                timesOpened = it.timesOpened + 1,
                                lastUsed = now
                            )
                        } else {
                            it
                        }
                    }
                } else {
                    current + RecentSearch(
                        title = title,
                        latitude = latitude,
                        longitude = longitude,
                        timesOpened = 1,
                        lastUsed = now
                    )
                }

            val finalList = updated
                .sortedWith(
                    compareByDescending<RecentSearch> {
                        it.timesOpened
                    }.thenByDescending {
                        it.lastUsed
                    }
                )
                .take(MAX_ITEMS)

            prefs[SEARCHES_KEY] = json.encodeToString(finalList)
        }
    }

    suspend fun clear() {
        context.recentSearchDataStore.edit {
            it.remove(SEARCHES_KEY)
        }
    }

    fun getTopRecent(
        list: List<RecentSearch>
    ): List<RecentSearch> {

        val monthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

        return list
            .filter { it.lastUsed > monthAgo }
            .sortedWith(
                compareByDescending<RecentSearch> {
                    it.timesOpened
                }.thenByDescending { it.lastUsed }
            )
            .take(4)
    }
}