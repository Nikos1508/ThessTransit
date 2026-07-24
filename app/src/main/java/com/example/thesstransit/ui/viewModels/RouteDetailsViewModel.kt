package com.example.thesstransit.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.LanguagePreferences
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.OsethTrackVehicle
import io.gitlab.mitsiosm.oseth.data.Coordinates
import io.gitlab.mitsiosm.oseth.data.DetailedRoute
import io.gitlab.mitsiosm.oseth.data.FirstStopTime
import io.gitlab.mitsiosm.oseth.data.Language
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.RouteId
import io.gitlab.mitsiosm.oseth.data.ShapeId
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.TripId
import io.gitlab.mitsiosm.oseth.data.Vehicle
import io.gitlab.mitsiosm.oseth.data.VehicleId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class RouteDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val api = Oseth(context)

    private val preferences = LanguagePreferences(application)

    private val _language = mutableStateOf(Language.GREEK)

    var isLoading = mutableStateOf(false)

    var errorMessage = mutableStateOf<String?>(null)

    var selectedShapeId = mutableStateOf<ShapeId?>(null)

    var selectedRouteId = mutableStateOf<RouteId?>(null)

    val stops = mutableStateListOf<Stop>()

    val departures = mutableStateListOf<FirstStopTime>()

    val tripHeadsigns = mutableStateMapOf<TripId, String>()

    var currentHeadsign by mutableStateOf<String?>(null)

    var route = mutableStateOf<Route?>(null)

    val routePolyline = mutableStateListOf<Coordinates>()

    val vehicles = mutableStateListOf<Vehicle>()

    val vehiclePositions = mutableStateListOf<Pair<Int,Float>>()

    val tripArrivalTimes = mutableStateMapOf<TripId, String>()

    private var tracker: OsethTrackVehicle? = null
    private var trackingJob: Job? = null
    private var loadingJob: Job? = null

    init {
        viewModelScope.launch {
            preferences.language.collect {
                _language.value = it
            }
        }
    }

    companion object {
        private const val TAG = "RouteDetails"
        const val EARTH_RADIUS = 6371000.0
    }

    @OptIn(ExperimentalTime::class)
    var selectedDate by mutableStateOf(
        Clock.System.todayIn( TimeZone.currentSystemDefault() )
    )
        private set

    @OptIn(ExperimentalTime::class)
    val weekDays: List<LocalDate>
        get() {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            return (0..6).map {
                today.plus(it, DateTimeUnit.DAY)
            }
        }

    fun loadRoute(route: Route) {

        if (selectedRouteId.value == route.id)
            return

        selectedRouteId.value = route.id

        this.route.value = route

        loadShape(route.id)
    }

    private fun startVehicleTracking(
        routeId: RouteId,
        shapeId: ShapeId
    ) {
        tracker?.stop()
        trackingJob?.cancel()

        tracker = OsethTrackVehicle(
            routeId = routeId,
            shapeId = shapeId,
            period = 8.seconds,
            context = getApplication<Application>().applicationContext
        )

        val tracker = tracker ?: return

        trackingJob = viewModelScope.launch {
            var lastUpdate = 0L

            for (coordinates in tracker.channel) {

                val now = System.currentTimeMillis()

                if (now - lastUpdate < 5000)
                    continue

                lastUpdate = now

                vehicles.clear()

                vehicles.addAll(
                    coordinates.mapIndexed { index, coordinate ->
                        Vehicle(
                            id = VehicleId(index.toString()),
                            latitude = coordinate.latitude,
                            longitude = coordinate.longitude,
                            bearing = 0.0
                        )
                    }
                )

                vehicles.forEachIndexed { index, vehicle ->
                    Log.d(
                        TAG,
                        "Vehicle[$index] (${vehicle.latitude}, ${vehicle.longitude})"
                    )
                }

                updateVehiclePositions()

                Log.d(
                    TAG,
                    "Tracker vehicles =  ${vehicles.size}"
                )

                vehicles.forEachIndexed { index, vehicle ->
                    Log.d(
                        TAG,
                        "Vehicle[$index] ${vehicle.latitude}, ${vehicle.longitude}"
                    )
                }
            }
        }
    }

    private fun updateVehiclePositions() {
        vehiclePositions.apply {
            clear()

            vehicles.forEach { vehicle ->
                calculateVehiclePosition(vehicle)?.let(::add)
            }
        }
    }

    private fun calculateVehiclePosition(
        vehicle: Vehicle
    ): Pair<Int, Float>? {

        if (stops.size < 2)
            return null

        var closestSegment = -1
        var closestDistance = Double.MAX_VALUE

        for (i in 0 until stops.lastIndex) {

            val start = stops[i]
            val end = stops[i + 1]

            val distance = distanceToSegment(
                vehicle.latitude,
                vehicle.longitude,
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude
            )

            if (distance < closestDistance) {
                closestDistance = distance
                closestSegment = i
            }

        }

        if (closestSegment == -1)
            return null

        val from = stops[closestSegment]
        val to = stops[closestSegment + 1]

        val totalDistance = distance(
            from.latitude,
            from.longitude,
            to.latitude,
            to.longitude
        )

        if (totalDistance == 0.0)
            return null

        val distanceFromStart = distance(
            from.latitude,
            from.longitude,
            vehicle.latitude,
            vehicle.longitude
        )

        val progress = (distanceFromStart / totalDistance)
            .coerceIn(0.0, 1.0)
            .toFloat()

        return closestSegment to progress

    }

    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            sin(dLat / 2) *
                    sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) *
                    sin(dLon / 2)

        val c =
            2 * atan2(
                sqrt(a),
                sqrt(1 - a)
            )

        return earthRadius * c

    }

    private fun distanceToSegment(
        px: Double,
        py: Double,
        ax: Double,
        ay: Double,
        bx: Double,
        by: Double
    ): Double {

        val dx = bx - ax
        val dy = by - ay

        if (dx == 0.0 && dy == 0.0) {
            return distance (
                px,
                py,
                ax,
                ay
            )
        }

        val t = ( ( (px - ax) * dx) + ( (py - ay) * dy) ) /
                ( dx * dx + dy * dy )

        val clamped = t.coerceIn(0.0, 1.0)

        val nearestLat = ax + clamped * dx
        val nearestLon = ay + clamped * dy

        return distance(
            px,
            py,
            nearestLat,
            nearestLon
        )

    }

    private fun stopVehicleTracking() {
        trackingJob?.cancel()
        tracker?.stop()

        trackingJob = null
        tracker = null

    }

    @SuppressLint("SuspiciousIndentation")
    @OptIn(ExperimentalTime::class)
    fun loadShape(
        routeId: RouteId,
        date: LocalDate = selectedDate
    ){

        Log.d(
            TAG,
            "Language = ${_language.value}"
        )

        Log.d(
            TAG,
            "Date = $selectedDate"
        )

        Log.d(
            TAG,
            "SelectedShape = ${selectedShapeId.value}"
        )

        selectedDate = date
        selectedRouteId.value = routeId

        loadingJob?.cancel()

        stopVehicleTracking()

        loadingJob = viewModelScope.launch {

            try {
                isLoading.value = true
                errorMessage.value = null

                stops.clear()
                tripHeadsigns.clear()
                vehicles.clear()
                vehiclePositions.clear()
                routePolyline.clear()
                departures.clear()

                val routeTrips = api.getTripsFromRoute(routeId)

                Log.d(
                    TAG,
                    "Route ${routeId.value} has ${routeTrips.size} directions"
                )

                routeTrips.forEachIndexed { index, trip ->

                    Log.d(
                        TAG,
                        "[$index] shape=${trip.shapeId.value} headsign=${trip.headsign}"
                    )

                }

                if (routeTrips.isEmpty()) {
                    errorMessage.value = "Δεν υπάρχουν διαθέσιμες κατευθύνσεις"

                    return@launch
                }

                val shapeId = selectedShapeId.value
                    ?.takeIf {
                        routeTrips.any { trip ->
                            trip.shapeId == it
                        }
                    }
                    ?: routeTrips.first().shapeId

                selectedShapeId.value = shapeId

                Log.d(
                    TAG,
                    "Selected shape: ${shapeId.value}"
                )

                Log.d(
                    TAG,
                    "Calling apiRouteInfo()..."
                )

                val routeStops = api.getStopsFromRoute(
                    routeId
                )

                Log.d(TAG, "========== ROUTE INFO ==========")
                Log.d(TAG, "Stops = ${routeStops.size}")
                Log.d(TAG, "Vehicles = ${vehicles.size}")
                Log.d(TAG, "Route = ${routeId.value}")
                Log.d(TAG, "Shape = ${shapeId.value}")
                Log.d(TAG, "================================")

                stops.addAll(routeStops)

                stops.forEachIndexed { index, stop ->
                    Log.d(
                        TAG,
                        "Stop[$index] (${stop.latitude}, ${stop.longitude})"
                    )
                }

                Log.d(
                    TAG,
                    "Loaded ${stops.size} stops"
                )

                updateVehiclePositions()

                Log.d(
                    TAG,
                    "Loaded ${vehicles.size} vehicles"
                )

                api.getShape(shapeId)?.let{
                    routePolyline.addAll(it)

                    Log.d(
                        TAG,
                        "Polyline loaded (${it.size} points)"
                    )
                } ?: Log.e(
                    TAG,
                    "Polyline is null"
                )

                Log.d(
                    TAG,
                    "Calling apiTimetableForToday()..."
                )

                try{

                    for (departure in departures) {
                        val tripDetails = api.getTrip(departure.tripId)

                        tripHeadsigns[tripDetails!!.id] = tripDetails.headsign
                        currentHeadsign = tripDetails.headsign

                        Log.d(
                            TAG,
                            "Trip = ${tripDetails.id.value}"
                        )

                        Log.d(
                            TAG,
                            "Headsign = ${tripDetails.headsign}"
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "apiTimetableForToday failed ${e.message}"
                    )
                }

                Log.d(
                    TAG,
                    "loaded ${departures.size} timetable entries"
                )

                Log.d(
                    TAG,
                    "Loading departures..."
                )

                try {
                    departures.addAll(
                        api.getFirstStopTimeFromRoute(
                            selectedDate,
                            routeId
                        )
                    )
                } catch(e: CancellationException) {
                    throw e
                } catch(e: Exception) {
                    Log.e(
                        TAG,
                        "getFirstStopTimeFromStart failed: ${e.message}"
                    )
                }

                departures.forEach{
                    Log.d(
                        TAG,
                        "Departure ${it.time}"
                    )
                }

                Log.d(
                    TAG,
                    "Loaded ${departures.size} departures"
                )

                Log.d(
                    TAG,
                    "Starting Vehicle Tracker..."
                )

                startVehicleTracking(
                    routeId,
                    shapeId
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                Log.e(
                    TAG,
                    "===== ERROR ====="
                )
                Log.e(
                    TAG,
                    e.stackTraceToString()
                )
                Log.e(
                    TAG,
                    "===== ERROR ====="
                )

                errorMessage.value = e.message ?: "Άγνωστο σφαλμα"

            } finally {
                isLoading.value = false

            }

        }
    }

    fun reloadCurrentRoute() {

        val routeId = selectedRouteId.value ?: return

        loadShape(
            routeId,
            selectedDate
        )
    }

    fun changeDirection(shapeId: ShapeId, routeId: RouteId) {
        selectedRouteId.value = routeId
        selectedShapeId.value = shapeId

        selectedRouteId.value?.let {
            loadShape(
                routeId = it,
                date = selectedDate)
        }
    }

    override fun onCleared() {
        stopVehicleTracking()
        super.onCleared()
    }

}

class RouteRepository(
    val context: Context
) {
    @OptIn(ExperimentalTime::class)
    suspend fun loadRoute(
        routeId: RouteId,
        date: LocalDate
    ): Pair<List<Stop>, List<FirstStopTime>> {
        val stops = Oseth(context).getStopsFromRoute(routeId)
        val departures = Oseth(context).getFirstStopTimeFromRoute(date, routeId)

        return Pair(
            stops,
            departures
        )
    }

}