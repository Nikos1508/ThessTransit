package com.example.thesstransit.ui.data.auth

data class LoginUiState (

    val email: String = "",
    val password: String= "",
    val passwordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null

)