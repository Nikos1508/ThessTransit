package com.example.thesstransit.ui.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

data class WalkingRoute(
    val distanceMeters: Double,
    val durationSeconds: Int
)

object WalkingRouter {
    private const val TAG = "WalkingRouter"
    private val client = OkHttpClient()

    suspend fun walkingRoute(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        straightLineFallbackMeters: Double
    ): WalkingRoute = withContext(Dispatchers.IO) {
        try {
            val url = "https://router.project-osrm.org/route/v1/walking/" +
                    "$fromLon,$fromLat;$toLon,$toLat?overview=false"

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "OSRM HTTP ${response.code}, using straight-line fallback")
                    return@withContext fallback(straightLineFallbackMeters)
                }

                val body = response.body?.string()
                    ?: return@withContext fallback(straightLineFallbackMeters)

                val routesArray = JSONObject(body).optJSONArray("routes")

                if (routesArray == null || routesArray.length() == 0) {
                    Log.w(TAG, "OSRM returned no route, using straight-line fallback")
                    return@withContext fallback(straightLineFallbackMeters)
                }

                val route = routesArray.getJSONObject(0)
                val distance = route.optDouble("distance", straightLineFallbackMeters)
                val duration = route.optDouble("duration", distance / 1.35).toInt()

                WalkingRoute(distanceMeters = distance, durationSeconds = duration)
            }
        } catch (e: IOException) {
            Log.w(TAG, "OSRM network call failed, using straight-line fallback", e)
            fallback(straightLineFallbackMeters)
        } catch (e: Exception) {
            Log.w(TAG, "OSRM response parsing failed, using straight-line fallback", e)
            fallback(straightLineFallbackMeters)
        }
    }

    private fun fallback(distanceMeters: Double) =
        WalkingRoute(
            distanceMeters = distanceMeters,
            durationSeconds = (distanceMeters / 1.35).toInt()
        )
}