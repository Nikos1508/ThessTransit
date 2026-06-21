package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import kotlinx.coroutines.launch

class RoutesViewModel : ViewModel() {

    var routes by mutableStateOf<List<Route>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

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

            isLoading = false
        }
    }
}