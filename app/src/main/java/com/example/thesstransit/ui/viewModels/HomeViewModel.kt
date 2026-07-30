package com.example.thesstransit.ui.viewModels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import io.gitlab.mitsiosm.oseth.Oseth

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val api = Oseth(application)

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