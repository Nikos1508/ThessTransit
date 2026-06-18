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
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.item.RoutesScreen
import com.example.thesstransit.ui.item.TicketsScreen
import com.example.thesstransit.ui.theme.ThessTransitTheme
import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object RoutesRoute
@Serializable object TicketsRoute

@Serializable object HowToGoRoute
@Serializable object LinesRoute
@Serializable object NearbyStopsRoute
@Serializable object LiveDeparturesRoute

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

                        composable<RoutesRoute> {
                            RoutesScreen(
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
                    }
                }
            }
        }
    }
}