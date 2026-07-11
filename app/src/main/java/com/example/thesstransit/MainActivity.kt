package com.example.thesstransit

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.thesstransit.ui.item.GroupRouteDetailsScreen
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.item.LocationPickerScreen
import com.example.thesstransit.ui.item.RouteDetailsScreen
import com.example.thesstransit.ui.item.RoutesScreen
import com.example.thesstransit.ui.item.StopDetailsScreen
import com.example.thesstransit.ui.item.TicketsScreen
import com.example.thesstransit.ui.theme.ThessTransitTheme
import com.example.thesstransit.ui.utils.groupRoutes
import com.example.thesstransit.ui.viewModels.RoutesViewModel
import com.example.thesstransit.ui.viewModels.StopDetailsViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import io.gitlab.mitsiosm.oseth.data.StopId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable object HomeRoute
@Serializable
data class RoutesRoute(
    val initialTab: String = "all"
)
@Serializable object TicketsRoute

@Serializable object HowToGoRoute
@Serializable object LinesRoute
@Serializable object NearbyStopsRoute
@Serializable object LiveDeparturesRoute

@Serializable
data class LocationPickerRoute(
    val type: String
)

@Serializable
data class RouteDetailsRoute(
    val route: Route
)

@Serializable
data class GroupDetailsRoute(
    val groupId: String
)

@Serializable
data class StopDetailsRoute(
    val stopId: String
)

val CustomRouteType = object : NavType<Route>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Route? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Route {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: Route) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: Route): String {
        return Uri.encode(Json.encodeToString(value))
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThessTransitTheme(darkTheme = true) {

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                onLoginClick = {
                                    navController.navigate(TicketsRoute)
                                },
                                onTicketsClick = {
                                    navController.navigate(TicketsRoute)
                                },
                                onHowToGoClick = {
                                    /* TODO */
                                },
                                onLinesClick = {
                                    navController.navigate(
                                        RoutesRoute()
                                    )
                                },
                                onNearbyStopsClick = {
                                    /*TODO*/
                                },
                                onLiveDeparturesClick = {
                                    /*TODO*/
                                },
                                onBuyTicketClick = {

                                },
                                onHomeClick = {
                                    navController.navigate(
                                        LocationPickerRoute(
                                            type = "home"
                                        )
                                    )
                                },
                                onWorkClick = {
                                    navController.navigate(
                                        LocationPickerRoute(
                                            type = "work"
                                        )
                                    )
                                },
                                onFavouritesClick = {
                                    navController.navigate(RoutesRoute("favorites"))
                                },
                                onNotificationsClick = {

                                },
                                onSettingsClick = {

                                }
                            )
                        }

                        composable<LocationPickerRoute> {
                            val args = it.toRoute<LocationPickerRoute>()

                            LocationPickerScreen(
                                type = args.type,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<TicketsRoute> {
                            TicketsScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<RoutesRoute> { entry ->

                            val args = entry.toRoute<RoutesRoute>()

                            RoutesScreen(
                                initialTab =
                                    if (args.initialTab == "favorites")
                                        2
                                    else
                                        0,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onRouteSelected = { route ->
                                    navController.navigate(
                                        RouteDetailsRoute(route)
                                    )
                                },
                                onGroupSelected = { groupId ->
                                    navController.navigate(
                                        GroupDetailsRoute(groupId)
                                    )
                                }
                            )
                        }

                        composable<RouteDetailsRoute>(
                            typeMap = mapOf(typeOf<Route>() to CustomRouteType)
                        ) { backStackEntry ->
                            val details = backStackEntry.toRoute<RouteDetailsRoute>()
                            val route = details.route

                            RouteDetailsScreen(
                                route = route,
                                onBackClick = { navController.popBackStack() },
                                onStopClick = { stop ->
                                    navController.navigate(
                                        StopDetailsRoute(
                                            stopId = stop.id.id
                                        )
                                    )
                                }
                            )

                        }

                        composable<GroupDetailsRoute> {
                            val details = it.toRoute<GroupDetailsRoute>()
                            val routesViewModel: RoutesViewModel = viewModel()
                            val groups = routesViewModel.routes.groupRoutes()
                            val group = groups.firstOrNull { it.groupId == details.groupId }

                            if (group == null) {
                                Text("Η κατηγορία δεν βρέθηκε.")
                            } else {
                                GroupRouteDetailsScreen(
                                    group = group,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                        }

                        composable<StopDetailsRoute> { backStackEntry ->
                            val stopId = backStackEntry.toRoute<StopDetailsRoute>().stopId
                            val vm: StopDetailsViewModel = viewModel()

                            val stop = remember(stopId) {
                                Stop(
                                    id = StopId(stopId),
                                    code = "",
                                    name = "Loading...",
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    sequence = 0u
                                )
                            }

                            LaunchedEffect(stopId) {
                                vm.load(stop)
                            }

                            StopDetailsScreen(
                                stop = stop,
                                viewModel = vm,
                                onBackClick = { navController.popBackStack() },
                                onRouteClick = { route ->
                                    navController.navigate(RouteDetailsRoute(route))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}