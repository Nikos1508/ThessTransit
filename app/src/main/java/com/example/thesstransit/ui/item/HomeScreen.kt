package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.AnimatedSearchBar
import com.example.thesstransit.ui.components.PremiumLoadingOverlay
import com.example.thesstransit.ui.components.RouteFiltersDialog
import com.example.thesstransit.ui.components.TutorialAnchor
import com.example.thesstransit.ui.data.SavedLocations
import com.example.thesstransit.ui.data.TutorialPages
import com.example.thesstransit.ui.data.TutorialState
import com.example.thesstransit.ui.data.TutorialTarget
import com.example.thesstransit.ui.data.tutorialTarget
import com.example.thesstransit.ui.utils.SharedKeys
import com.example.thesstransit.ui.viewModels.AuthViewModel
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import com.example.thesstransit.ui.viewModels.HomeViewModel
import com.example.thesstransit.ui.viewModels.TutorialViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.milliseconds


object Guard {
    @OptIn(ExperimentalAtomicApi::class)
    val busy = AtomicBoolean(false)
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAtomicApi::class)
@Composable
fun HomeScreen(
    onLoginClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHowToGoClick: () -> Unit = {},
    onTicketsClick: () -> Unit = {},
    onLinesClick: () -> Unit = {},
    onNearbyStopsClick: () -> Unit = {},
    onLiveDeparturesClick: () -> Unit = {},
    onBuyTicketClick: () -> Unit = {},
    onFavouritesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onMetroClick: () -> Unit,
    onWorkClick: () -> Unit = {},
    favoritesViewModel: FavoritesViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    tutorialViewModel: TutorialViewModel,

    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
){

    val defaultSubtitle = stringResource(R.string.quick_set_location)

    val tiles = listOf(
        FeatureTile(title = stringResource(R.string.tile_tickets),    subtitle = "", Icons.Outlined.LocalActivity, onClick = onTicketsClick,       target = TutorialTarget.TICKETS),
        FeatureTile(title = stringResource(R.string.tile_buy_ticket),     subtitle = "", Icons.Outlined.QrCode2,       onClick = onBuyTicketClick,     target = TutorialTarget.BUY_TICKET),
        FeatureTile(title = stringResource(R.string.tile_metro_lines),        subtitle = "", Icons.Outlined.Train,         onClick = onMetroClick,         target = TutorialTarget.METRO),
        FeatureTile(title = stringResource(R.string.tile_favorite_routes), subtitle = "", Icons.Outlined.Favorite,      onClick = onFavouritesClick,    target = TutorialTarget.FAVORITE_ROUTES),
        FeatureTile(title = stringResource(R.string.tile_notifications),         subtitle = "", Icons.Outlined.Notifications, onClick = onNotificationsClick, target = TutorialTarget.NOTIFICATIONS),
        FeatureTile(title = stringResource(R.string.tile_settings),            subtitle = "", Icons.Outlined.Settings,      onClick = onSettingsClick,      target = TutorialTarget.SETTINGS)
    )

    var showFilters by remember { mutableStateOf(false) }

    val favoriteRoutes by favoritesViewModel.favorites.collectAsState()
    val favoriteGroups by favoritesViewModel.favoriteGroups.collectAsState()
    val totalFavorites = favoriteRoutes.size + favoriteGroups.size

    val context = LocalContext.current
    LaunchedEffect(Unit) {

        if (homeViewModel.hasSynced)
            return@LaunchedEffect

        if (!Guard.busy.compareAndSet(expectedValue = false, newValue = true))
            return@LaunchedEffect

        homeViewModel.startLoading()

        try {
            withContext(Dispatchers.IO) {
                homeViewModel.api.sync()
            }
        } finally {
            Guard.busy.store(false)
            homeViewModel.stopLoading()
        }
    }

    val tutorialCompleted by tutorialViewModel
        .tutorialCompleted
        .collectAsState()

    val savedLocations = remember(context) {
        SavedLocations(context)
    }

    val home by savedLocations.home.collectAsState(
        initial = Triple(null, null, null)
    )

    val work by savedLocations.work.collectAsState(
        initial = Triple(null, null, null)
    )

    val tutorialState = remember {
        TutorialState()
    }

    var showTutorial = !tutorialCompleted

    LaunchedEffect(showTutorial) {
        if (showTutorial) {
            tutorialState.reset()
        }
    }

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ){
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HeaderSection(
                    currentUser = authViewModel.user.collectAsState().value,
                    onLoginClick = onLoginClick,
                    onProfileClick = {
                        // TODO Profile Screen
                    }
                )
            }
            item {
                Spacer( modifier = Modifier.height(8.dp) )

                with(sharedTransitionScope){
                    TutorialAnchor(
                        target = TutorialTarget.SEARCH,
                        tutorialState = tutorialState
                    ) {
                        AnimatedSearchBar(
                            onClick = onSearchClick,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .sharedElement(
                                    rememberSharedContentState(
                                        key = SharedKeys.SEARCH_BAR
                                    ),
                                    animatedVisibilityScope = animatedContentScope
                                ),
                            onFilteredClick = { showFilters = true }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuickAccessRow(
                    favoriteCount = totalFavorites,
                    onHomeClick = onHomeClick,
                    onWorkClick = onWorkClick,
                    onFavouritesClick = onFavouritesClick,
                    homeSubtitle = home.first ?: defaultSubtitle,
                    workSubtitle = work.first ?: defaultSubtitle,
                    tutorialState = tutorialState
                )
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                MainFeatureGrid(
                    onHowToGoClick = onHowToGoClick,
                    onLinesClick = onLinesClick,
                    onNearbyStopsClick = onNearbyStopsClick,
                    onLiveDeparturesClick = onLiveDeparturesClick,
                    tutorialState = tutorialState
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = stringResource(R.string.section_updates_news)
                )
            }
            item {
                TutorialAnchor(
                    target = TutorialTarget.AI,
                    tutorialState = tutorialState
                ) {
                    AIUpdateSection()
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = stringResource(R.string.section_extra_features)
                )
            }
            item {
                FeatureTilesGrid(
                    tilesList = tiles,
                    tutorialState = tutorialState
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showFilters) {
            RouteFiltersDialog(
                onDismiss = { showFilters = false },
                onApplyFilters = { filterResults ->

                    println("Applied Filters: $filterResults")
                }
            )
        }

        if (showTutorial) {
            TutorialOverlay(
                tutorialState = tutorialState,
                onSkip = {
                    tutorialViewModel.completeTutorial()
                },
                onNext = {
                    if (tutorialState.currentStep == TutorialPages.lastIndex) {
                        tutorialViewModel.completeTutorial()
                    } else {
                        haptic.performHapticFeedback(
                            HapticFeedbackType.TextHandleMove
                        )

                        tutorialState.currentStep++
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = homeViewModel.isLoading.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            PremiumLoadingOverlay()

        }
    }
}

@Composable
fun HeaderSection(
    currentUser: UserInfo?,
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var showGreekTitle: Boolean by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4500.milliseconds)
            showGreekTitle = !showGreekTitle
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.aerialviewwhitetower),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 600f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
                AnimatedContent(
                    targetState = currentUser,
                    label = "account"
                ) { user ->

                    if (user == null) {
                        OutlinedButton(
                            onClick = onLoginClick,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .height(36.dp)
                                .align(Alignment.TopEnd)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = stringResource(R.string.login),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = onProfileClick,
                            modifier = Modifier
                                .height(38.dp)
                                .align(Alignment.TopEnd),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(
                                text = user.email ?: "Profile",
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.welcome_to),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AnimatedContent(
                        targetState = showGreekTitle,
                        transitionSpec = {
                            (
                                    fadeIn(
                                        animationSpec = tween(500)
                                    ) + slideInVertically(
                                        animationSpec = tween(500),
                                        initialOffsetY = { it / 2 }
                                    )
                                    ).togetherWith(
                                    fadeOut(
                                        animationSpec = tween(400)
                                    ) + slideOutVertically(
                                        animationSpec = tween(400),
                                        targetOffsetY = { -it / 2 }
                                    )
                                ) using SizeTransform(clip = false)
                        },
                        label = "ThessAnimation"
                    ) { greek ->

                        Text(
                            text = if (greek) "Θες" else "Thess",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    Text(
                        text = "Transit",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.app_subtitle),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun QuickAccessRow(
    favoriteCount: Int,
    onHomeClick: () -> Unit,
    onWorkClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    homeSubtitle: String,
    workSubtitle: String,
    tutorialState: TutorialState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAccessItem(
            title = stringResource(R.string.quick_home),
            subtitle = homeSubtitle,
            icon = Icons.Outlined.Home,
            iconColor = Color.White,
            onClick = onHomeClick,
            modifier = Modifier.weight(1f),
            tutorialTarget = TutorialTarget.HOME,
            tutorialState = tutorialState
        )

        QuickAccessItem(
            title = stringResource(R.string.quick_work),
            subtitle = workSubtitle,
            icon = Icons.Outlined.Work,
            iconColor = MaterialTheme.colorScheme.primary,
            onClick = onWorkClick,
            modifier = Modifier.weight(1f),
            tutorialTarget = TutorialTarget.WORK,
            tutorialState = tutorialState

        )

        QuickAccessItem(
            title = stringResource(R.string.quick_favorites),
            subtitle = stringResource(R.string.routes_count_format, favoriteCount),
            icon = Icons.Outlined.Star,
            iconColor = Color(0xFFFFD700),
            onClick = onFavouritesClick,
            modifier = Modifier.weight(1f),
            tutorialTarget = TutorialTarget.FAVORITES,
            tutorialState = tutorialState
        )
    }
}

@Composable
fun QuickAccessItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    tutorialTarget: TutorialTarget? = null,
    tutorialState: TutorialState? = null
) {
    Surface(
        modifier =
            modifier
                .then(
                    if (tutorialTarget != null && tutorialState != null)
                        Modifier.tutorialTarget(
                            tutorialTarget,
                            tutorialState
                        )
                    else
                        Modifier
                )
                .height(82.dp)
                .bounceClick { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1
            )
        }
    }
}

@Composable
fun MainFeatureGrid(
    onHowToGoClick: () -> Unit,
    onLinesClick: () -> Unit,
    onNearbyStopsClick: () -> Unit,
    onLiveDeparturesClick: () -> Unit,
    tutorialState: TutorialState
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TutorialAnchor(
                target = TutorialTarget.HOW_TO_GO,
                tutorialState = tutorialState
            ) {
                MainFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.feature_how_to_go_title),
                    subtitle = stringResource(R.string.feature_how_to_go_subtitle),
                    icon = Icons.Outlined.Route,
                    onClick = onHowToGoClick
                )
            }

            TutorialAnchor(
                target = TutorialTarget.LINES,
                tutorialState = tutorialState
            ) {
                MainFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.feature_lines_title),
                    subtitle = stringResource(R.string.feature_lines_subtitle),
                    icon = Icons.Outlined.DirectionsBus,
                    onClick = onLinesClick
                )
            }
        }

        Spacer( modifier = Modifier.height(12.dp) )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TutorialAnchor(
                target = TutorialTarget.STOPS,
                tutorialState = tutorialState
            ) {
                MainFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.feature_nearby_stops_title),
                    subtitle = stringResource(R.string.feature_nearby_stops_subtitle),
                    icon = Icons.Outlined.LocationOn,
                    onClick = onNearbyStopsClick
                )
            }

            TutorialAnchor(
                target = TutorialTarget.LIVE,
                tutorialState = tutorialState
            ) {
                MainFeatureCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.feature_live_departures_title),
                    subtitle = stringResource(R.string.feature_live_departures_subtitle),
                    icon = Icons.Outlined.AccessTime,
                    onClick = onLiveDeparturesClick
                )
            }
        }
    }
}

@Composable
fun MainFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .bounceClick { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

data class AIUpdate(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun getAiUpdates(): List<AIUpdate> {
    return listOf(
        AIUpdate(
            title = stringResource(R.string.update_traffic_title),
            description = stringResource(R.string.update_traffic_desc),
            icon = Icons.Outlined.DirectionsCar
        ),
        AIUpdate(
            title = stringResource(R.string.update_works_title),
            description = stringResource(R.string.update_works_desc),
            icon = Icons.Outlined.Notifications
        )
    )
}

@Composable
fun AIUpdateSection(){
    val updates = getAiUpdates()
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        updates.forEach {
            AIUpdateCard(update = it)
        }
    }
}
@Composable
fun AIUpdateCard(
    update: AIUpdate
){
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = update.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = update.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = update.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun FeatureTilesGrid(
    tilesList: List<FeatureTile>,
    tutorialState: TutorialState
) {

    val chunkedTiles = tilesList.chunked(3)

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chunkedTiles.take(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { tile ->
                    SmallFeatureTile(
                        modifier = Modifier.weight(1f),
                        tile = tile,
                        tutorialState = tutorialState
                    )
                }
            }
        }
    }
}

@Composable
fun SmallFeatureTile(
    modifier: Modifier = Modifier,
    tile: FeatureTile,
    tutorialState: TutorialState
) {
    Surface(
        modifier = modifier
            .tutorialTarget(
                tile.target,
                tutorialState
            )
            .height(85.dp)
            .bounceClick { tile.onClick() },
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tile.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 13.sp
            )
        }
    }
}

data class FeatureTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val target: TutorialTarget,
    val onClick: () -> Unit
)

fun Modifier.bounceClick(
    tintColor: Color = Color.Black,
    maxOverlayAlpha: Float = 0.08f,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 1500f),
        label = "BounceAnimation"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (isPressed) maxOverlayAlpha else 0f,
        animationSpec = spring(stiffness = 1000f),
        label = "BounceColorOverlay"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .drawWithContent {
            drawContent()
            if (overlayAlpha > 0f) {
                drawRect(
                    color = tintColor.copy(alpha = overlayAlpha),
                    size = size
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        awaitRelease()
                    } finally {
                        isPressed = false
                    }
                },
                onTap = { onClick() }
            )
        }
}