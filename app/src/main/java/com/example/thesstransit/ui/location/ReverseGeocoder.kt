package com.example.thesstransit.ui.location

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReverseGeocoder(
    private val context: Context
) {
    suspend fun getName(
        lat: Double,
        lon: Double
    ): String? {

        return withContext(
            Dispatchers.IO
        ){
            try {
                val geocoder =
                    Geocoder(context)

                val result =
                    geocoder.getFromLocation(
                        lat,
                        lon,
                        1
                    )

                result?.firstOrNull()
                    ?.getAddressLine(0)
            } catch(e: Exception){
                null
            }
        }
    }
}