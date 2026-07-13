package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

private val Context.appLanguageDataStore by preferencesDataStore("app_language")

class AppLanguagePreferences(
    private val context: Context
) {
    companion object {
        private  val LANGUAGE =
            stringPreferencesKey("app_language")
    }

    val language: Flow<String> =
        context.appLanguageDataStore.data.map { pref ->
            pref[LANGUAGE] ?: "el"
        }

    suspend fun save(language: String) {
        context.appLanguageDataStore.edit {
            it[LANGUAGE] = language
        }
    }
}