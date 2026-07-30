package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.auth.AuthManager
import com.example.thesstransit.ui.data.auth.LoginUiState
import com.example.thesstransit.ui.data.repository.AuthRepository
import com.example.thesstransit.ui.utils.SupabaseErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel (
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy (
                password = password,
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(
                passwordVisible = !it.passwordVisible
            )
        }
    }

    private fun validateEmail(): Boolean {

        val email = uiState.value.email.trim()

        if (email.isBlank()) {
            _uiState.update {
                it.copy(
                    emailError = "Το email είναι υποχρεωτικό πεδίο."
                )
            }
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update {
                it.copy(
                    emailError = "Μη έγκυρο email"
                )
            }
            return false
        }

        _uiState.update {
            it.copy(
                emailError = null
            )
        }

        return true
    }

    private fun validatePassword(): Boolean {

        val password = uiState.value.password

        if (password.isBlank()) {
            _uiState.update {
                it.copy(
                    passwordError = "Ο κωδικός είναι υποχρεωτικό πεδίο"
                )
            }
            return false
        }

        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    passwordError = "Ο κωδικός πρέπει να είναι τουλάχιστον 6 χαρακτήρες"
                )
            }

            return false
        }

        _uiState.update {
            it.copy(
                passwordError = null
            )
        }

        return true
    }

    fun login() {
        if (!validateEmail() || !validatePassword()) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val result = repository.login(
                email = uiState.value.email.trim(),
                password = uiState.value.password
            )

            result.onSuccess {
                AuthManager.loadUser()
                _uiState.update {
                    it.copy(
                        isLoading=false,
                        isLoggedIn=true
                    )
                }
            }

            result.onFailure { error ->

                println(
                    "SUPABASE LOGIN ERROR: ${error.message}"
                )

                _uiState.update {
                    it.copy (
                        isLoading = false,
                        errorMessage =
                            when{
                                error is NoClassDefFoundError || error is ClassNotFoundException ->
                                    "Σφάλμα συμβατότητας (Datetime). Επικοινώνησε με τον προγραμματιστή."
                                error.message?.contains("Invalid login") == true ->
                                    "Λάθος email ή κωδικός"
                                else ->
                                    SupabaseErrorMapper.map(error)
                            }
                    )
                }
            }
        }
    }
}