package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val isLoading = mutableStateOf(false)

    var hasSynced = false
        private set

    fun startLoading() {
        isLoading.value = true
    }

    fun stopLoading() {
        isLoading.value = false
        hasSynced = true
    }

}