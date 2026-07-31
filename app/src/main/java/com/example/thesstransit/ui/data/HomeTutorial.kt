package com.example.thesstransit.ui.data

import android.graphics.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
    LINES,
    NEARBY,
    LIVE,
    AI,
    EXTRA

}

data class TutorialPage(
    val step: TutorialStep,
    val title: String,
    val description: String
)

val TutorialPages = listOf(
    TutorialPage(
        TutorialStep.SEARCH,
        "Search",
        "Search any stop, line or destination in the greater area of Thessaloniki"
    ),

    TutorialPage(
        TutorialStep.HOME,
        "Home",
        "Search any stop, line or destination in the greater area of Thessaloniki"
    ),

    TutorialPage(
        TutorialStep.WORK,
        "Work",
        "Search any stop, line or destination in the greater area of Thessaloniki"
    ),

    TutorialPage(
        TutorialStep.FAVORITES,
        "Favorites",
        "Search any stop, line or destination in the greater area of Thessaloniki"
    ),
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

//fun Modifier.tutorialTarget(
//    target: TutorialTarget,
//    tutorialState: TutorialState
//): Modifier = onGloballyPositioned {
//
//    val position = it.positionInRoot()
//
//    tutorialState.register(
//        target,
//        Rect(
//            position,
//
//            it.size.toSize()
//        )
//
//    )
//
//}