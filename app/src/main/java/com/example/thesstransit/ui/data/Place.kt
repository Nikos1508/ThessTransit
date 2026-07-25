package com.example.thesstransit.ui.data

data class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: PlaceType = PlaceType.SEARCH
)


enum class PlaceType {
    CURRENT_LOCATION,
    HOME,
    WORK,
    SEARCH
}