package com.example.thesstransit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.item.RoutesScreen
import com.example.thesstransit.ui.item.TicketsScreen
import com.example.thesstransit.ui.theme.ThessTransitTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.gitlab.mitsiosm.oseth.data.Route
import android.net.Uri
import androidx.navigation.NavType
import com.example.thesstransit.ui.item.GroupRouteDetailsScreen
import kotlin.reflect.typeOf
import com.example.thesstransit.ui.item.RouteDetailsScreen
import io.gitlab.mitsiosm.oseth.data.RouteId

@Serializable object HomeRoute
@Serializable object RoutesRoute
@Serializable object TicketsRoute

@Serializable object HowToGoRoute
@Serializable object LinesRoute
@Serializable object NearbyStopsRoute
@Serializable object LiveDeparturesRoute

@Serializable
data class RouteDetailsRoute(
    val route: Route
)

@Serializable
data class GroupDetailsRoute(
    val groupId: String
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
                                onHowToGoClick = { /* TODO */},

                                onLinesClick = {
                                    navController.navigate(RoutesRoute)
                                },

                                onNearbyStopsClick = { /*TODO*/ },
                                onLiveDeparturesClick = { /*TODO*/ }
                            )
                        }

                        composable<TicketsRoute> {
                            TicketsScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<RoutesRoute> {
                            RoutesScreen(
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
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}