package com.example.thesstransit.ui.data

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Work
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.thesstransit.R

enum class TutorialStep {

    LOGIN,
    SEARCH,
    HOME,
    WORK,
    FAVORITES,
    HOW_TO_GO,
    LINES,
    STOPS,
    LIVE,
    AI,
    TICKETS,
    BUY_TICKET,
    METRO,
    FAVORITE_ROUTES,
    NOTIFICATIONS,
    SETTINGS,
}

enum class TutorialTarget {

    LOGIN,
    SEARCH,
    HOME,
    WORK,
    FAVORITES,
    HOW_TO_GO,
    LINES,
    STOPS,
    LIVE,
    AI,
    TICKETS,
    BUY_TICKET,
    METRO,
    FAVORITE_ROUTES,
    NOTIFICATIONS,
    SETTINGS

}

data class TutorialAnchorInfo(
    val rect: Rect,
    val itemIndex: Int
)

enum class CardPosition {
    TOP,
    BOTTOM
}

data class TutorialPage(
    val step: TutorialStep,
    val target: TutorialTarget,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val icon: ImageVector,
    val listIndex: Int,
    val cardPosition: CardPosition = CardPosition.TOP
)

val TutorialPages = listOf(

    TutorialPage(
        TutorialStep.LOGIN,
        TutorialTarget.LOGIN,
        titleRes = R.string.tutorial_title_login,
        descriptionRes = R.string.tutorial_desc_login,
        Icons.Outlined.Person,
        listIndex = 0,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.SEARCH,
        TutorialTarget.SEARCH,
        titleRes = R.string.tutorial_title_search,
        descriptionRes = R.string.tutorial_desc_search,
        icon = Icons.Outlined.Search,
        listIndex = 1,
        cardPosition = CardPosition.BOTTOM,
    ),

    TutorialPage(
        TutorialStep.HOME,
        TutorialTarget.HOME,
        titleRes = R.string.tutorial_title_home,
        descriptionRes = R.string.tutorial_desc_home,
        listIndex = 2,
        cardPosition = CardPosition.BOTTOM,
        icon = Icons.Outlined.Home
    ),

    TutorialPage(
        TutorialStep.WORK,
        TutorialTarget.WORK,
        titleRes = R.string.tutorial_title_work,
        descriptionRes = R.string.tutorial_desc_work,
        Icons.Outlined.Work,
        listIndex = 2,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.FAVORITES,
        TutorialTarget.FAVORITES,
        titleRes = R.string.tutorial_title_favorites,
        descriptionRes = R.string.tutorial_desc_favorites,
        Icons.Outlined.Star,
        listIndex = 2,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        step = TutorialStep.HOW_TO_GO,
        target = TutorialTarget.HOW_TO_GO,
        titleRes = R.string.tutorial_title_how_to_go,
        descriptionRes = R.string.tutorial_desc_how_to_go,
        Icons.Outlined.Route,
        listIndex = 3,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.LINES,
        TutorialTarget.LINES,
        titleRes = R.string.tutorial_title_lines,
        descriptionRes = R.string.tutorial_desc_lines,
        Icons.Outlined.DirectionsBus,
        listIndex = 3,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.STOPS,
        TutorialTarget.STOPS,
        titleRes = R.string.tutorial_title_stops,
        descriptionRes = R.string.tutorial_desc_stops,
        Icons.Outlined.LocationOn,
        listIndex = 3
    ),

    TutorialPage(
        TutorialStep.LIVE,
        TutorialTarget.LIVE,
        titleRes = R.string.tutorial_title_live,
        descriptionRes = R.string.tutorial_desc_live,
        Icons.Outlined.AccessTime,
        listIndex = 3,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.AI,
        TutorialTarget.AI,
        titleRes = R.string.tutorial_title_ai,
        descriptionRes = R.string.tutorial_desc_ai,
        Icons.Outlined.AutoAwesome,
        listIndex = 5,
        cardPosition = CardPosition.BOTTOM
    ),

    TutorialPage(
        TutorialStep.TICKETS,
        TutorialTarget.TICKETS,
        titleRes = R.string.tutorial_title_tickets,
        descriptionRes = R.string.tutorial_desc_tickets,
        Icons.Outlined.LocalActivity,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.BUY_TICKET,
        TutorialTarget.BUY_TICKET,
        titleRes = R.string.tutorial_title_buy_ticket,
        descriptionRes = R.string.tutorial_desc_buy_ticket,
        Icons.Outlined.QrCode2,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.METRO,
        TutorialTarget.METRO,
        titleRes = R.string.tutorial_title_metro,
        descriptionRes = R.string.tutorial_desc_metro,
        Icons.Outlined.Train,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.FAVORITE_ROUTES,
        TutorialTarget.FAVORITE_ROUTES,
        titleRes = R.string.tutorial_title_favorite_routes,
        descriptionRes = R.string.tutorial_desc_favorite_routes,
        Icons.Outlined.Favorite,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.NOTIFICATIONS,
        TutorialTarget.NOTIFICATIONS,
        titleRes = R.string.tutorial_title_notifications,
        descriptionRes = R.string.tutorial_desc_notifications,
        Icons.Outlined.Notifications,
        listIndex = 7
    ),

    TutorialPage(
        TutorialStep.SETTINGS,
        TutorialTarget.SETTINGS,
        titleRes = R.string.tutorial_title_settings,
        descriptionRes = R.string.tutorial_desc_settings,
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
        targets[target] = TutorialAnchorInfo(rect, itemIndex)
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
    val rect = it.boundsInRoot()
    tutorialState.register(target, rect, itemIndex)
}