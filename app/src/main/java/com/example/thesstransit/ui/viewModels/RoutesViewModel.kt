package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Language
import io.gitlab.mitsiosm.oseth.data.Route
import kotlinx.coroutines.launch

class RoutesViewModel(
    application: Application
) : AndroidViewModel(application) {

    var routes by mutableStateOf<List<Route>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    private val preferences = LanguagePreferences(application)

    init {
        viewModelScope.launch {
            preferences.language.collect { language ->
                loadRoutes(language)
            }
        }
    }

    private fun loadRoutes(
        language: Language
    ) {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            try {
                Oseth()
                    .getRoutes()
                    .onSuccess { results ->
                        routes = results.sortedBy {
                            it.shortName
                        }
                    }
                    .onFailure {
                        errorMessage = it.message ?: "Unknown Error"
                    }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error loading routes"
            }

            isLoading = false
        }
    }
}