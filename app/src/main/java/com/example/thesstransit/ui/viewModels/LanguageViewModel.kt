package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.data.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LanguageViewModel(
    application: Application
): AndroidViewModel(application) {

    private val preferences =
        LanguagePreferences(application)

    private val _language =
        MutableStateFlow(Language.GREEK)

    val language: StateFlow<Language>
        get() = _language

    init {
        viewModelScope.launch {
            preferences.language.collect {
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

            preferences.save(newLanguage)
        }
    }

}