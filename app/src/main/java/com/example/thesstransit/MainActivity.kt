package com.example.thesstransit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.thesstransit.ui.data.AppThemePreferences
import com.example.thesstransit.ui.data.TutorialPreferences
import com.example.thesstransit.ui.data.TutorialViewModelFactory
import com.example.thesstransit.ui.item.GroupRouteDetailsScreen
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.item.LocationPickerScreen
import com.example.thesstransit.ui.item.LoginScreen
import com.example.thesstransit.ui.item.MetroScreen
import com.example.thesstransit.ui.item.RegisterScreen
import com.example.thesstransit.ui.item.RouteDetailsScreen
import com.example.thesstransit.ui.item.RoutesScreen
import com.example.thesstransit.ui.item.SearchScreen
import com.example.thesstransit.ui.item.SettingsScreen
import com.example.thesstransit.ui.item.StopDetailsScreen
import com.example.thesstransit.ui.item.TicketsScreen
import com.example.thesstransit.ui.theme.ThessTransitTheme
import com.example.thesstransit.ui.utils.groupRoutes
import com.example.thesstransit.ui.viewModels.AppTheme
import com.example.thesstransit.ui.viewModels.AuthViewModel
import com.example.thesstransit.ui.viewModels.RoutesViewModel
import com.example.thesstransit.ui.viewModels.StopDetailsViewModel
import com.example.thesstransit.ui.viewModels.TutorialViewModel
import io.gitlab.mitsiosm.oseth.data.Route
import io.gitlab.mitsiosm.oseth.data.Stop
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable object HomeRoute
@Serializable
data class RoutesRoute(
    val initialTab: String = "all"
)
@Serializable object TicketsRoute

@Serializable object LoginRoute

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
object RegisterRoute

@Serializable
object SettingsRoute

@Serializable
object MetroRoute

@Serializable
data class StopDetailsRoute(
    val stop: Stop
)

@Serializable
object SearchRoute

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

val CustomStopType = object : NavType<Stop>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Stop? {
        return bundle.getString(key)?.let {
            Json.decodeFromString(it)
        }
    }

    override fun parseValue(value: String): Stop {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: Stop) {
        bundle.putString(
            key,
            Json.encodeToString(value)
        )
    }

    override fun serializeAsValue(value: Stop): String {
        return Uri.encode(
            Json.encodeToString(value)
        )
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            val themePreferences = remember {
                AppThemePreferences(context)
            }

            val appTheme by themePreferences
                .theme
                .collectAsState(initial = AppTheme.SYSTEM)

            @OptIn(ExperimentalSharedTransitionApi::class)
            SharedTransitionLayout {

                val navController = rememberNavController()

                ThessTransitTheme(
                    darkTheme = when (appTheme) {
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                    }
                ) {

                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->

                        val tutorialViewModel: TutorialViewModel =
                            viewModel(
                                factory = TutorialViewModelFactory(
                                    TutorialPreferences(context)
                                )
                            )

                        NavHost(
                            navController = navController,
                            startDestination = HomeRoute,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<HomeRoute> {
                                HomeScreen(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedContentScope = this,
                                    onLoginClick = {
                                        navController.navigate(LoginRoute)
                                    },
                                    onTicketsClick = {
                                        navController.navigate(TicketsRoute)
                                    },
                                    onSearchClick = {
                                        navController.navigate(SearchRoute)
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
                                        navController.navigate(SettingsRoute)
                                    },
                                    onMetroClick = {
                                        navController.navigate(MetroRoute)
                                    },
                                    tutorialViewModel = tutorialViewModel
                                )
                            }

                            composable<LoginRoute>{

                                LoginScreen(
                                    onLoginClick={
                                        navController.navigate(HomeRoute){
                                            popUpTo(LoginRoute){
                                                inclusive=true
                                            }
                                        }
                                    },
                                    onRegisterClick={
                                        navController.navigate(RegisterRoute)
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

                            composable<RegisterRoute>{
                                RegisterScreen(
                                    onRegisterSuccess = {
                                        navController.navigate(LoginRoute){
                                            popUpTo(RegisterRoute){
                                                inclusive=true
                                            }
                                        }
                                    },
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable<SettingsRoute> {
                                val authViewModel:AuthViewModel=viewModel()
                                val user by authViewModel.user.collectAsState()

                                SettingsScreen(
                                    user = user,
                                    onBackClick={
                                        navController.popBackStack()
                                    },
                                    onLoginClick={
                                        navController.navigate(LoginRoute)
                                    },
                                    onLogoutClick={
                                        authViewModel.logout()
                                        navController.navigate(LoginRoute){ popUpTo(0) }
                                    },
                                    tutorialViewModel = tutorialViewModel
                                )
                            }

                            composable<MetroRoute> {
                                MetroScreen(
                                    onBackClick = { navController.popBackStack() }
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
                                            StopDetailsRoute(stop)
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

                            composable<SearchRoute> {
                                SearchScreen(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedContentScope = this,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable<StopDetailsRoute>(
                                typeMap = mapOf(typeOf<Stop>() to CustomStopType)
                            ) { backStackEntry ->
                                val stop = backStackEntry.toRoute<StopDetailsRoute>().stop
                                val vm: StopDetailsViewModel = viewModel()

                                LaunchedEffect(stop) {
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
}