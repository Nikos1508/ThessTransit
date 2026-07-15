package com.example.thesstransit.ui.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.thesstransit.ui.viewModels.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(
    name = "theme_preferences"
)

class AppThemePreferences(
    private val context: Context
) {
    companion object {
        private val THEME = stringPreferencesKey("theme")
    }

    val theme: Flow<AppTheme> = context.themeDataStore.data.map {  preferences ->

        when (preferences[THEME] ?: AppTheme.SYSTEM.name) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }

    }

    suspend fun saveTheme(theme: AppTheme) {

        context.themeDataStore.edit { preferences ->
            preferences[THEME] = theme.name
        }

    }
}