package com.example.thesstransit.ui.network

data class JourneyOption(
    val depart: String,
    val arrival: String,
    val durationMinutes: Int,
    val numTransfers: Int,
    val totalWalkSeconds: Int,
    val reliabilityNote: String?,
    val legs: List<JourneyLeg>
)

data class JourneyLeg(
    val mode: String,
    val routeShortName: String? = null,
    val boardStopName: String? = null,
    val alightStopName: String? = null,
    val fromStopName: String? = null,
    val toStopName: String? = null,
    val departure: String,
    val arrival: String,
    val walkSeconds: Int? = null
)