package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("saved_locations")

class SavedLocations(context: Context) {

    private val dataStore = context.dataStore

    companion object {

        val HOME_NAME = stringPreferencesKey("home_name")
        val HOME_LAT = doublePreferencesKey("home_lat")
        val HOME_LON = doublePreferencesKey("home_lon")

        val WORK_NAME = stringPreferencesKey("work_name")
        val WORK_LAT = doublePreferencesKey("work_lat")
        val WORK_LON = doublePreferencesKey("work_lon")

    }

    suspend fun saveHome(
        name: String,
        lat: Double,
        lon: Double
    ) {
        dataStore.edit {
            it[HOME_NAME] = name
            it[HOME_LAT] = lat
            it[HOME_LON] = lon
        }
    }

    suspend fun saveWork(
        name: String,
        lat: Double,
        lon: Double
    ) {
        dataStore.edit {
            it[WORK_NAME] = name
            it[WORK_LAT] = lat
            it[WORK_LON] = lon
        }
    }

    val home = dataStore.data.map {

        Triple(
            it[HOME_NAME],
            it[HOME_LAT],
            it[HOME_LON]
        )

    }

    val work = dataStore.data.map {

        Triple(
            it[WORK_NAME],
            it[WORK_LAT],
            it[WORK_LON]
        )

    }

}