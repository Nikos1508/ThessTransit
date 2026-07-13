package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.AppLanguagePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppLanguageViewModel(
    application: Application
): AndroidViewModel(application) {

    private val preferences = AppLanguagePreferences(application)

    private val _language = MutableStateFlow("el")

    val language: StateFlow<String>
        get() = _language

    init {
        viewModelScope.launch {
            preferences.language.collect {
                _language.value = it

                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(it)
                )
            }
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val newLanguage =
                if (_language.value == "el")
                    "en"
                else
                    "el"

            preferences.save(newLanguage)
        }
    }
}