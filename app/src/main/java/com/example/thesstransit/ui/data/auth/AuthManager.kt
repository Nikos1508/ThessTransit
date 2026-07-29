package com.example.thesstransit.ui.data.auth

import com.example.thesstransit.ui.data.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)

    val currentUser = _currentUser.asStateFlow()

    suspend fun initialize(){
        _currentUser.value =
            SupabaseClient
                .client
                .auth
                .currentUserOrNull()
    }

    fun loadUser(){
        _currentUser.value =
            SupabaseClient
                .client
                .auth
                .currentUserOrNull()
    }

    suspend fun signOut(){
        SupabaseClient
            .client
            .auth
            .signOut()
        _currentUser.value=null
    }
}