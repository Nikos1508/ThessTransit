package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "favorites"
)

class RoutePreferences(
    private val context: Context
){

    companion object {
        private val FAVORITES_KEY =
            stringSetPreferencesKey("favorites_routes")
    }

    val favoriteRoutes: Flow<Set<String>> =
        context.dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun toggleFavorite(routeId: String) {
        context.dataStore.edit { preferences ->

            val current = preferences[FAVORITES_KEY]?.toMutableSet()?:mutableSetOf()

            if (current.contains(routeId)) {
                current.remove(routeId)
            } else {
                current.add(routeId)
            }

            preferences[FAVORITES_KEY] = current
        }
    }

    suspend fun addFavourite(routeId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY]?.toMutableSet()?:mutableSetOf()

            current.add(routeId)

            preferences[FAVORITES_KEY] = current
        }
    }

    suspend fun removeFavourite(routeId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY]?.toMutableSet()?:mutableSetOf()

            current.remove(routeId)

            preferences[FAVORITES_KEY] = current
        }
    }
}
