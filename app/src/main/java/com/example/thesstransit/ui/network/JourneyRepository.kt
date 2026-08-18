package com.example.thesstransit.ui.network

import io.gitlab.mitsiosm.oseth.Oseth
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

class JourneyRepository(
    private val oseth: Oseth = Oseth()
) {
    private data class RouteData(
        val route: Route,
        val stops: List<Stop>
    )
    private var cachedRoutes: List<RouteData>? = null

    private suspend fun loadRoutes(): List<RouteData> =
        withContext(Dispatchers.IO) {

            cachedRoutes?.let {
                return@withContext it
            }

            val routes = oseth.getRoutes()

            val data = routes.mapNotNull { route ->

                try {
                    val stops = oseth.getStopsFromRoute(route.id)

                    if (stops.isNotEmpty()) {
                        RouteData(
                            route = route,
                            stops = stops
                        )
                    } else {
                        null
                    }

                } catch (_: Exception) {
                    null
                }
            }

            cachedRoutes = data
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
        maxTransfers: Int = 3
    ): Result<List<JourneyOption>> = runCatching {

        val routes = loadRoutes()

        if (routes.isEmpty()) {
            return@runCatching emptyList()
        }

        val originStops = findNearestStops(
            latitude = originLat,
            longitude = originLon,
            routes,
            limit = 6
        )

        val destinationStops = findNearestStops(
            latitude = destLat,
            longitude = destLon,
            routes,
            limit = 6
        )

        if (originStops.isEmpty() || destinationStops.isEmpty()) {
            return@runCatching emptyList()
        }

        val destinationIds = destinationStops
            .map { it.first.id }
            .toSet()

        val departure = parseTime(departTime)

        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        val today = localDateTime.date
        val results = mutableListOf<JourneyOption>()

        for ((originStop, originDistance) in originStops) {

            val servingRoutes = routes.filter { routeData ->
                routeData.stops.any {
                    it.id == originStop.id
                }
            }

            for (routeData in servingRoutes) {

                val times = try {
                    oseth.getStopTimesAfterTimeFromRoute(
                        date = today,
                        route = routeData.route.id,
                        stop = originStop.id,
                        after = departure
                    )
                } catch (_: Exception) {
                    emptyList()
                }

                for (time in times.take(4)) {

                    val tripStops = try {
                        oseth.getStopsFromTrip(time.trip)
                    } catch (_: Exception) {
                        emptyList()
                    }

                    if (tripStops.isEmpty()) { continue }

                    val originIndex = tripStops.indexOfFirst {
                        it.stop.id == originStop.id
                    }

                    if (originIndex < 0) { continue }

                    val destinationStop = tripStops
                        .drop(originIndex + 1)
                        .firstOrNull {
                            it.stop.id in destinationIds
                        }

                    if (destinationStop != null) {

                        val departureTime = time.time
                        val arrivalTime = destinationStop.time

                        val destinationDistance =
                            distanceMeters(
                                destinationStop.stop.latitude,
                                destinationStop.stop.longitude,
                                destLat,
                                destLon
                            )

                        val walkingSeconds =
                            ((originDistance + destinationDistance) / 1.35)
                                .toInt()

                        val duration =
                            minutesBetween(
                                departureTime,
                                arrivalTime
                            ) + walkingSeconds / 60

                        results += JourneyOption(
                            depart = formatTime(departureTime),
                            arrival = formatTime(arrivalTime),
                            durationMinutes = duration,
                            numTransfers = 0,
                            totalWalkSeconds = walkingSeconds,
                            reliabilityNote = null,
                            legs = listOf(
                                JourneyLeg(
                                    mode = "walk",
                                    departure = formatTime(departureTime),
                                    arrival = formatTime(departureTime),
                                    fromStopName = "Origin",
                                    toStopName = originStop.name,
                                    walkSeconds = walkingSeconds / 2
                                ),
                                JourneyLeg(
                                    mode = "transit",
                                    routeShortName = routeData.route.shortName,
                                    boardStopName = originStop.name,
                                    alightStopName = destinationStop.stop.name,
                                    departure = formatTime(departureTime),
                                    arrival = formatTime(arrivalTime)
                                ),
                                JourneyLeg(
                                    mode = "walk",
                                    departure = formatTime(arrivalTime),
                                    arrival = formatTime(arrivalTime),
                                    fromStopName = destinationStop.stop.name,
                                    toStopName = "Destination",
                                    walkSeconds = walkingSeconds / 2
                                )
                            )
                        )
                    }
                }
            }
        }

        results
            .distinctBy {
                "${it.depart}-${it.arrival}-${it.legs.joinToString { leg -> leg.routeShortName ?: leg.mode }}"
            }
            .sortedBy {
                it.durationMinutes
            }
            .take(5)
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