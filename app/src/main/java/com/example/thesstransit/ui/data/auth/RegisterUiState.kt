package com.example.thesstransit.ui.data.auth

data class RegisterUiState(

    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordVisible: Boolean = false,
    val confirmPasswordError: String? = null
)