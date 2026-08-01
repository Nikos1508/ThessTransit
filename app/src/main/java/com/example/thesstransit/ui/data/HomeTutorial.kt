package com.example.thesstransit.ui.data

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize

enum class TutorialStep {

    SEARCH,

    HOME,
    WORK,
    FAVORITES,

    HOW_TO_GO,
    LINES,
    STOPS,
    LIVE,

    AI,

    EXTRA,

    TICKETS,
    BUY_TICKET,
    METRO,
    FAVORITE_ROUTES,
    NOTIFICATIONS,
    SETTINGS,

    DONE

}

enum class TutorialTarget {

    SEARCH,

    HOME,
    WORK,
    FAVORITES,

    HOW_TO_GO,
    LINES,
    STOPS,
    LIVE,

    AI,

    EXTRA,

    TICKETS,
    BUY_TICKET,
    METRO,
    FAVORITE_ROUTES,
    NOTIFICATIONS,
    SETTINGS,

    DONE

}

data class TutorialAnchorInfo(
    val rect: Rect,
    val itemIndex: Int
)

data class TutorialPage(
    val step: TutorialStep,
    val target: TutorialTarget,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val listIndex: Int
)

val TutorialPages = listOf(

    TutorialPage(
        TutorialStep.SEARCH,
        TutorialTarget.SEARCH,
        "Search anything",
        "Find stops, routes, destinations and useful transport information instantly.",
        icon = Icons.Outlined.Search,
        listIndex = 1
    ),

    TutorialPage(
        TutorialStep.HOME,
        TutorialTarget.HOME,
        "Home",
        "Save your home location for faster navigation.",
        Icons.Outlined.Home,
        listIndex = 2
    ),

    TutorialPage(
        TutorialStep.WORK,
        TutorialTarget.WORK,
        "Work",
        "Access your daily commute instantly.",
        Icons.Outlined.Work,
        listIndex = 2
    ),

    TutorialPage(
        TutorialStep.FAVORITES,
        TutorialTarget.FAVORITES,
        "Favorites",
        "Keep your favourite routes nearby.",
        Icons.Outlined.Star,
        listIndex = 2
    ),

    TutorialPage(
        TutorialStep.HOW_TO_GO,
        TutorialTarget.HOW_TO_GO,
        "How to go",
        "Plan your trip with public transport.",
        Icons.Outlined.Route,
        listIndex = 3
    ),

    TutorialPage(
        TutorialStep.LINES,
        TutorialTarget.LINES,
        "Lines",
        "Explore all available transport lines.",
        Icons.Outlined.DirectionsBus,
        listIndex = 3
    ),

    TutorialPage(
        TutorialStep.STOPS,
        TutorialTarget.STOPS,
        "Nearby stops",
        "Find stops close to your location.",
        Icons.Outlined.LocationOn,
        listIndex = 3
    ),

    TutorialPage(
        TutorialStep.LIVE,
        TutorialTarget.LIVE,
        "Live departures",
        "Track upcoming departures in real time.",
        Icons.Outlined.AccessTime,
        listIndex = 3
    ),

    TutorialPage(
        TutorialStep.AI,
        TutorialTarget.AI,
        "AI Updates",
        "Receive intelligent transport information.",
        Icons.Outlined.AutoAwesome,
        listIndex = 5
    ),

    TutorialPage(
        TutorialStep.TICKETS,
        TutorialTarget.TICKETS,
        "Tickets",
        "Access your transport tickets quickly.",
        Icons.Outlined.LocalActivity,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.BUY_TICKET,
        TutorialTarget.BUY_TICKET,
        "Buy ticket",
        "Purchase tickets directly from the application.",
        Icons.Outlined.QrCode2,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.METRO,
        TutorialTarget.METRO,
        "Metro lines",
        "Explore Thessaloniki metro lines and stations.",
        Icons.Outlined.Train,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.FAVORITE_ROUTES,
        TutorialTarget.FAVORITE_ROUTES,
        "Favourite routes",
        "Keep your most used routes always available.",
        Icons.Outlined.Favorite,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.NOTIFICATIONS,
        TutorialTarget.NOTIFICATIONS,
        "Notifications",
        "Receive important transport updates.",
        Icons.Outlined.Notifications,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.SETTINGS,
        TutorialTarget.SETTINGS,
        "Settings",
        "Customize your ThessTransit experience.",
        Icons.Outlined.Settings,
        listIndex = 7
    )
)

class TutorialState {
    var currentStep by mutableIntStateOf(0)
    val targets = mutableStateMapOf<TutorialTarget, TutorialAnchorInfo>()

    fun register(
        target: TutorialTarget,
        rect: Rect,
        itemIndex: Int
    ) {
        targets[target] =
            TutorialAnchorInfo(
                rect,
                itemIndex
            )
    }

    fun reset() {
        currentStep = 0
    }

    val currentPage
        get() = TutorialPages[currentStep]
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.tutorialTarget(
    target: TutorialTarget,
    tutorialState: TutorialState,
    itemIndex: Int
): Modifier = onGloballyPositioned {

    val position = it.positionInRoot()

    tutorialState.register(
        target,
        Rect(
            offset = position,
            size = it.size.toSize()
        ),
        itemIndex
    )

}