package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "tutorial_preferences"
)

class TutorialPreferences(
    private val context: Context
) {

    private val tutorialCompletedKey =
        booleanPreferencesKey(
            "tutorial_completed"
        )

    val tutorialCompleted: Flow<Boolean> =
        context.dataStore.data.map {preferences ->
            preferences[tutorialCompletedKey] ?: false
        }

    suspend fun setTutorialCompleted(
        completed: Boolean
    ) {
        context.dataStore.edit {preferences ->
            preferences[tutorialCompletedKey] = completed
        }
    }

}