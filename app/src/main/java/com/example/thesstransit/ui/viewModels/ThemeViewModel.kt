package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.AppThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

class ThemeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences = AppThemePreferences(application)

    val theme = preferences.theme.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    fun setTheme(theme: AppTheme) {

        viewModelScope.launch {
            preferences.saveTheme(theme)
        }

    }
}