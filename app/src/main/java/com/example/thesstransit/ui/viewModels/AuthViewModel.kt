package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel(){
    val user = AuthManager.currentUser
    private val _initialized = MutableStateFlow(false)

    val initialized = _initialized.asStateFlow()

    init {
        viewModelScope.launch {

            AuthManager.initialize()

            _initialized.value=true
        }
    }

    fun logout(){
        viewModelScope.launch {
            AuthManager.signOut()
        }
    }

}