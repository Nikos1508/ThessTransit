package com.example.thesstransit.ui.network

import android.content.Context
import android.util.Log
import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class JourneyRepository(
    context: Context
) {
    private val oseth = Oseth(context)

    companion object {
        // Tag για να μπαίνει στα Logcat
        private const val TAG = "JourneyRepository"

        // Μέγιστη απόσταση που θεωρούμε λογική για περπάτημα από/προς μία στάση
        private const val MAX_WALK_DISTANCE_METERS = 1500.0

        //Πόσες κοντινές στάσεις εξετάζουμε ως πιθανές στάσεις επιβίβασης
        private const val ORIGIN_STOP_LIMIT = 10

        //Πόσα επόμενα δρομολόγια εξετάζουμε από κάθε στάση
        private const val TRIPS_PER_ROUTE = 6
    }

    private data class RouteData(
        val route: Route,
        val stops: List<Stop>
    )

    private var cachedRoutes: List<RouteData>? = null

    /* Φορτώνει όλες τις γραμμές και τις στάσεις τους.
     *
     * Το αποτέλεσμα γίνεται cache ώστε να μη ζητάμε ξανά
     * όλο το δίκτυο κάθε φορά που ψάχνουμε διαδρομή.
     */
    private suspend fun loadRoutes(): List<RouteData> =
        withContext(Dispatchers.IO) {

            cachedRoutes?.let {
                Log.d(TAG, "Using cached routes: ${it.size}")
                return@withContext it
            }

            Log.d(TAG, "Loading routes from Oseth...")

            val routes = oseth.getRoutes()

            Log.d(TAG, "Oseth returned ${routes.size} routes")

            val data = routes.mapNotNull { route ->
                try {
                    val stops = oseth.getStopsFromRoute(route.id)

                    if (stops.isNotEmpty()) {
                        Log.d(TAG, "Route ${route.shortName}: ${stops.size} stops")
                        RouteData(
                            route = route,
                            stops = stops
                        )
                    } else {
                        Log.d(TAG, "Route ${route.shortName} has no stops")
                        null
                    }

                } catch (e: Exception) {
                    Log.d(TAG, "Failed loading route ${route.shortName}", e)
                    null
                }
            }

            cachedRoutes = data
            Log.d(TAG, "Successfully loaded ${data.size} route")
            data
        }

    @OptIn(ExperimentalTime::class)
    suspend fun findJourneys(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        departTime: String,
        @Suppress("UNUSED_PARAMETER")
        optimizeFor: String = "arrival_time",
        @Suppress("UNUSED_PARAMETER")
        walkPenalty: Double = 1.0,
        @Suppress("UNUSED_PARAMETER")
        maxTransfers: Int = 0
    ): Result<List<JourneyOption>> = runCatching {

        Log.d(TAG, "================================")
        Log.d(TAG, "JOURNEY SEARCH")
        Log.d(TAG, "Origin: $originLat, $originLon")
        Log.d(TAG, "Destination: $destLat, $destLon")
        Log.d(TAG, "Departure: $departTime")
        Log.d(TAG, "================================")

        val routes = loadRoutes()

        if (routes.isEmpty()) {
            Log.w(TAG, "No routes available")
            return@runCatching emptyList()
        }

        val originStops = findNearestStops(
            latitude = originLat,
            longitude = originLon,
            routes = routes,
            limit = ORIGIN_STOP_LIMIT
        )

        Log.d(TAG, "Nearest origin stops: ")
        originStops.forEach { (stop, distance) ->
            Log.d(TAG, "${stop.name} (${stop.id}) - ${distance.toInt()}m")
        }

        if (originStops.isEmpty()) {
            Log.d(TAG, "No origin stops found")
            return@runCatching emptyList()
        }

//        val destinationStops = findNearestStops(
//            latitude = destLat,
//            longitude = destLon,
//            routes,
//            limit = 6
//        )
//
//        val destinationIds = destinationStops
//            .map { it.first.id }
//            .toSet()

        val departure = parseTime(departTime)

        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val today = localDateTime.date

        val results = mutableListOf<JourneyOption>()

        for ((originStop, originDistance) in originStops) {

            if (originDistance > MAX_WALK_DISTANCE_METERS) { continue }
            Log.d(TAG, "--------------------------------")
            Log.d(TAG, "Checking origin stop: ${originStop.name} (${originDistance.toInt()}m)")

            val servingRoutes = routes.filter { routeData ->
                routeData.stops.any { it.id == originStop.id }
            }
            Log.d(TAG, "Serving route: " + servingRoutes.joinToString { it.route.shortName })

            for (routeData in servingRoutes) {
                Log.d(TAG, "Checking route ${routeData.route.shortName}")

                val times = try {
                    oseth.getStopTimesAfterTimeFromRoute(
                        date = today,
                        route = routeData.route.id,
                        stop = originStop.id,
                        after = departure
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed loading times for route ${routeData.route.shortName} at ${originStop.name}", e)
                    emptyList()
                }

                Log.d(TAG, "Found ${times.size} departures")

                for (time in times.take(TRIPS_PER_ROUTE)) {

                    Log.d(TAG, "Trip ${time.trip} departs at ${formatTime(time.time)}")

                    val tripStops = try {
                        oseth.getStopsFromTrip(time.trip)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed getting trip stops for ${time.trip}", e)
                        emptyList()
                    }

                    if (tripStops.isEmpty()) {
                        Log.w(TAG, "Trip ${time.trip} has no stops")
                        continue
                    }

                    val originIndex = tripStops.indexOfFirst {
                        it.stop.id == originStop.id
                    }

                    if (originIndex < 0) {
                        Log.w(TAG, "Origin stop ${originStop.id} not found in trip ${time.trip}")
                        continue
                    }

                    Log.d(TAG, "Origin index: $originIndex / ${tripStops.size}")

                    val destinationCandidate = tripStops
                        .drop(originIndex + 1)
                        .minByOrNull { tripStop ->
                            distanceMeters(
                                tripStop.stop.latitude,
                                tripStop.stop.longitude,
                                destLat,
                                destLon
                            )
                        }

                    if (destinationCandidate == null) {
                        Log.d(TAG, "No stops after origin")
                        continue
                    }

                    val destinationDistance = distanceMeters(
                        destinationCandidate.stop.latitude,
                        destinationCandidate.stop.longitude,
                        destLat,
                        destLon
                    )

                    Log.d(TAG, "Best destination stop: ${destinationCandidate.stop.name} (${destinationDistance.toInt()}m)")

                    if (destinationDistance > MAX_WALK_DISTANCE_METERS) {
                        Log.d(TAG, "Destination stop too far. Skipping trip.")
                        continue
                    }

                    val departureTime = time.time
                    val arrivalTime = destinationCandidate.time

                    val walkToOriginSeconds = (originDistance / 1.35).toInt()
                    val walkFromDestinationSeconds = (destinationDistance / 1.35).toInt()
                    val totalWalkSeconds = walkToOriginSeconds + walkFromDestinationSeconds

                    val transitMinutes = minutesBetween(departureTime, arrivalTime)
                    val totalDurationMinutes = transitMinutes + totalWalkSeconds / 60

                    Log.d(TAG, "DIRECT JOURNEY FOUND!")
                    Log.d(TAG, "${originStop.name} -> ${routeData.route.shortName} -> ${destinationCandidate.stop.name}")
                    Log.d(TAG, "Departure: " + formatTime(departureTime))
                    Log.d(TAG, "Arrival: " + formatTime(arrivalTime))
                    Log.d(TAG, "Transit: ${transitMinutes}min")
                    Log.d(TAG, "Walking: ${totalWalkSeconds}sec")

                    results += JourneyOption(
                        depart = formatTime(departureTime),
                        arrival = formatTime(arrivalTime),
                        durationMinutes = totalDurationMinutes,
                        numTransfers = 0,
                        totalWalkSeconds = totalWalkSeconds,
                        reliabilityNote = null,
                        legs = listOf(
                            JourneyLeg(
                                mode = "walk",
                                departure = formatTime(departureTime),
                                arrival = formatTime(departureTime),
                                fromStopName = "Origin",
                                toStopName = originStop.name,
                                walkSeconds = walkToOriginSeconds
                            ),
                            JourneyLeg(
                                mode = "transit",
                                routeShortName = routeData.route.shortName,
                                boardStopName = originStop.name,
                                alightStopName = destinationCandidate.stop.name,
                                departure = formatTime(departureTime),
                                arrival = formatTime(arrivalTime)
                            ),
                            JourneyLeg(
                                mode = "walk",
                                departure = formatTime(arrivalTime),
                                arrival = formatTime(arrivalTime),
                                fromStopName = destinationCandidate.stop.name,
                                toStopName = "Destination",
                                walkSeconds = walkFromDestinationSeconds
                            )
                        )
                    )
                }
            }
        }

        val finalResults = results
            .distinctBy { "${it.depart}-${it.arrival}-" + it.legs.joinToString { leg -> leg.routeShortName ?: leg.mode }}
            .sortedWith(compareBy<JourneyOption> { it.arrival }.thenBy { it.durationMinutes })
            .take(5)

        Log.d(TAG, "================================")
        Log.d(TAG, "JOURNEY SEARCH FINISHED")
        Log.d(TAG, "Found ${finalResults.size} journeys")
        finalResults.forEach { journey ->
            Log.d(TAG, "RESULT: ${journey.depart} -> ${journey.arrival}, ${journey.durationMinutes}min")
        }
        Log.d(TAG, "================================")

        finalResults
    }

    private fun findNearestStops(
        latitude: Double,
        longitude: Double,
        routes: List<RouteData>,
        limit: Int
    ): List<Pair<Stop, Double>> {
        return routes
            .asSequence()
            .flatMap { it.stops.asSequence() }
            .distinctBy { it.id }
            .map {
                it to distanceMeters(
                    latitude,
                    longitude,
                    it.latitude,
                    it.longitude
                )
            }
            .sortedBy { it.second }
            .take(limit)
            .toList()
    }

    private fun distanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2) *
            sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    @OptIn(ExperimentalTime::class)
    private fun parseTime(value: String): LocalTime {
        return try {
            LocalTime.parse(value)
        } catch (_: Exception) {
            val now = Clock.System.now()
            now.toLocalDateTime(TimeZone.currentSystemDefault()).time
        }
    }


    private fun formatTime(time: LocalTime): String {
        return "%02d:%02d".format(
            time.hour,
            time.minute
        )
    }

    private fun minutesBetween(
        start: LocalTime,
        end: LocalTime
    ):Int {
        val startSeconds =
            start.hour * 60 * 60 +
            start.minute * 60 +
            start.second

        val endSeconds =
            end.hour * 60 * 60 +
            end.minute * 60 +
            end.second

        var seconds = endSeconds - startSeconds

        if (seconds < 0) {
            seconds += 24 * 60 * 60
        }

        return seconds/60
    }
}