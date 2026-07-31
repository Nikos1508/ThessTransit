package com.example.thesstransit.ui.data

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
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
    DONE

}

enum class TutorialTarget {

    SEARCH,
    HOME,
    WORK,
    FAVORITES,
    HOW_TO_GO,
    STOPS,
    LINES,
    LIVE,
    AI,
    EXTRA

}

data class TutorialPage(
    val step: TutorialStep,
    val target: TutorialTarget,
    val title: String,
    val description: String,
    val icon: ImageVector
)

val TutorialPages = listOf(

    TutorialPage(
        TutorialStep.SEARCH,
        TutorialTarget.SEARCH,
        "Search anything",
        "Find stops, routes, destinations and useful transport information instantly.",
        icon = Icons.Outlined.Search
    ),

    TutorialPage(
        TutorialStep.HOME,
        TutorialTarget.HOME,
        "Home",
        "Save your home location for faster navigation.",
        Icons.Outlined.Home
    ),

    TutorialPage(
        TutorialStep.WORK,
        TutorialTarget.WORK,
        "Work",
        "Access your daily commute instantly.",
        Icons.Outlined.Work
    ),

    TutorialPage(
        TutorialStep.FAVORITES,
        TutorialTarget.FAVORITES,
        "Favorites",
        "Keep your favourite routes nearby.",
        Icons.Outlined.Star
    ),

    TutorialPage(
        TutorialStep.HOW_TO_GO,
        TutorialTarget.HOW_TO_GO,
        "How to go",
        "Plan your trip with public transport.",
        Icons.Outlined.Route
    ),

    TutorialPage(
        TutorialStep.LINES,
        TutorialTarget.LINES,
        "Lines",
        "Explore all available transport lines.",
        Icons.Outlined.DirectionsBus
    ),

    TutorialPage(
        TutorialStep.STOPS,
        TutorialTarget.STOPS,
        "Nearby stops",
        "Find stops close to your location.",
        Icons.Outlined.LocationOn
    ),

    TutorialPage(
        TutorialStep.LIVE,
        TutorialTarget.LIVE,
        "Live departures",
        "Track upcoming departures in real time.",
        Icons.Outlined.AccessTime
    ),

    TutorialPage(
        TutorialStep.AI,
        TutorialTarget.AI,
        "AI Updates",
        "Receive intelligent transport information.",
        Icons.Outlined.AutoAwesome
    ),

    TutorialPage(
        TutorialStep.EXTRA,
        TutorialTarget.EXTRA,
        "More features",
        "Tickets, notifications and settings.",
        Icons.Outlined.Settings
    )
)

class TutorialState {
    var currentStep by mutableIntStateOf(0)
    val targets = mutableStateMapOf<TutorialTarget, Rect>()

    fun register(
        target: TutorialTarget,
        rect: Rect
    ) {
        targets[target] = rect
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.tutorialTarget(
    target: TutorialTarget,
    tutorialState: TutorialState
): Modifier = onGloballyPositioned {

    val position = it.positionInRoot()

    tutorialState.register(
        target,
        Rect(
            offset = position,
            size = it.size.toSize()
        )
    )

}