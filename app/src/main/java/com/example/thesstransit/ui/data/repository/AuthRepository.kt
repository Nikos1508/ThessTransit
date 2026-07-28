package com.example.thesstransit.ui.data.repository

import io.github.jan.supabase.auth.providers.builtin.Email
import com.example.thesstransit.ui.data.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {
            SupabaseClient.client.auth.signInWith(
                Email
            ) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
}