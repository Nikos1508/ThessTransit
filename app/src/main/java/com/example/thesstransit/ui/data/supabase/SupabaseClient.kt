package com.example.thesstransit.ui.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://irxvqjpxxbtgfraybkll.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlyeHZxanB4eGJ0Z2ZyYXlia2xsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUyMjgwMDIsImV4cCI6MjEwMDgwNDAwMn0.-S12hPbRy1amwkgD0gRf8L0bbhiTOc6CZrVOzPwmg0I"
    ) {
        install(Auth) {
            alwaysAutoRefresh = true
        }
        install(Postgrest)
    }


}