package com.example.thesstransit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thesstransit.ui.components.ThessTransitBottomBar
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.item.RoutesScreen
import com.example.thesstransit.ui.item.TicketsScreen
import com.example.thesstransit.ui.theme.ThessTransitTheme
import kotlinx.serialization.Serializable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination

@Serializable
object HomeRoute

@Serializable
object RoutesRoute

@Serializable
object TicketsRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThessTransitTheme(darkTheme = true) {

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ThessTransitBottomBar(
                            selectedIndex = when {
                                currentDestination?.hasRoute<RoutesRoute>() == true -> 0
                                currentDestination?.hasRoute<HomeRoute>() == true -> 1
                                currentDestination?.hasRoute<TicketsRoute>() == true -> 2
                                else -> 1
                            },
                            onItemSelected = { newIndex ->
                                when (newIndex) {
                                    0 -> navController.navigate(RoutesRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                        }
                                        launchSingleTop = true
                                    }
                                    1 -> navController.navigate(HomeRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                        }
                                        launchSingleTop = true
                                    }
                                    2 -> navController.navigate(TicketsRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<RoutesRoute> {
                            RoutesScreen()
                        }

                        composable<HomeRoute> {
                            HomeScreen(
                                onLoginClick = {
                                    /* Πατώντας το login, σε πάει στα Εισιτήρια μέσω Navigation  TODO change */
                                    navController.navigate(TicketsRoute)
                                },
                                onHowToGoClick = { /*TODO*/ },
                                onLinesClick = { /*TODO*/ },
                                onNearbyStopsClick = { /*TODO*/ },
                                onLiveDeparturesClick = { /*TODO*/ }
                            )
                        }

                        composable<TicketsRoute> {
                            TicketsScreen()
                        }
                    }
                }
            }
        }
    }
}