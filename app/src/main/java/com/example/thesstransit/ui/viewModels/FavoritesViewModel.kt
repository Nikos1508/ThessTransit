package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.RoutePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val prefs =
        RoutePreferences(application)

    val favorites = prefs.favoriteRoutes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val favoriteGroups =
        prefs.favoriteGroups
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    fun toggleFavorite(routeId: String) {

        viewModelScope.launch {
            prefs.toggleFavorite(routeId)
        }
    }

    fun toggleFavoriteGroup(groupId: String) {

        viewModelScope.launch {
            prefs.toggleFavoriteGroup(groupId)
        }
    }
}