package com.example.thesstransit.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(
    private val context: Context
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {

        val manager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val providers = manager.getProviders(true)

        var best: Location? = null

        for(provider in providers) {
            val location = manager.getLastKnownLocation(provider)

            if (location != null) {
                if (
                    best == null ||
                    location.accuracy < best!!.accuracy
                ) {
                    best = location
                }
            }
        }
        return best
    }
}