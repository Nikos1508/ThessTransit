package com.example.thesstransit.navigation

sealed class Screen(
    val route: String
){
    object Tickets : Screen("tickets")
    object Home : Screen("home")
    object Routes : Screen("routes")
}