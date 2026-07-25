package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.data.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LanguageViewModel(
    application: Application
): AndroidViewModel(application) {
    private val apiPreferences = LanguagePreferences(application)

    private val _language = MutableStateFlow(Language.GREEK)

    val language: StateFlow<Language> = _language

    init {
        viewModelScope.launch {
            apiPreferences.language.collect {
                _language.value = it
            }
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val newLanguage =
                if (_language.value == Language.GREEK)
                    Language.ENGLISH
                else
                    Language.GREEK

            apiPreferences.save(newLanguage)

            val locale =
                if (newLanguage == Language.ENGLISH)
                    "en"
                else
                    "el"

            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(locale)
            )
        }
    }
}