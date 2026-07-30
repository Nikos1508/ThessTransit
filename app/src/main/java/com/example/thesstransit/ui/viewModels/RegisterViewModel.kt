package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.auth.RegisterUiState
import com.example.thesstransit.ui.data.repository.AuthRepository
import com.example.thesstransit.ui.utils.SupabaseErrorMapper
import com.example.thesstransit.ui.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.thesstransit.ui.data.auth.AuthManager

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository()
):ViewModel(){

    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email:String){
        _uiState.update {
            it.copy(
                email=email,
                emailError=null,
                errorMessage=null
            )
        }
    }

    fun onPasswordChange(password:String){
        _uiState.update {
            it.copy(
                password=password,
                passwordError=null,
                errorMessage=null
            )
        }
    }

    fun onConfirmPasswordChange(password:String){
        _uiState.update {
            it.copy(
                confirmPassword=password,
                confirmPasswordError=null,
                errorMessage=null
            )
        }
    }

    fun togglePasswordVisibility(){
        _uiState.update {
            it.copy(
                passwordVisible =
                    !it.passwordVisible
            )
        }
    }

    fun toggleConfirmPasswordVisibility(){
        _uiState.update {
            it.copy(
                confirmPasswordVisible = !it.confirmPasswordVisible
            )
        }
    }

    fun register(){
        val state = uiState.value

        if(state.isLoading)
            return

        clearErrors()

        val emailError =
            ValidationUtils.validateEmail(
                state.email
            )

        val passwordError =
            ValidationUtils.validatePassword(
                state.password
            )

        val confirmError =
            if(
                state.password !=
                state.confirmPassword
            )
                "Οι κωδικοί δεν ταιριάζουν"
            else
                null

        if(
            emailError != null ||
            passwordError != null ||
            confirmError != null
        ){
            _uiState.update {
                it.copy(
                    emailError=emailError,
                    passwordError=passwordError,
                    confirmPasswordError=confirmError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading=true
                )
            }

            repository.register(
                email = state.email.trim(),
                password = state.password
            )
                .onSuccess {
                    AuthManager.loadUser()
                    _uiState.update {
                        it.copy(
                            isLoading=false,
                            isRegistered=true
                        )
                    }
                }
                .onFailure {error ->
                    _uiState.update {
                        it.copy(
                            isLoading=false,
                            errorMessage =
                                when{
                                    error is NoClassDefFoundError || error is ClassNotFoundException ->
                                        "Σφάλμα συμβατότητας (Datetime). Επικοινώνησε με τον προγραμματιστή."
                                    else ->
                                        SupabaseErrorMapper.map(error)
                                }
                        )
                    }
                }
        }
    }

    private fun clearErrors(){
        _uiState.update {
            it.copy(
                emailError=null,
                passwordError=null,
                confirmPasswordError=null,
                errorMessage=null
            )
        }
    }
}