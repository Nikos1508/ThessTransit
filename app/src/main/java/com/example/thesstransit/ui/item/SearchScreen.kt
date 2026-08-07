package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.SwapVerticalCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.SearchField
import com.example.thesstransit.ui.data.Place
import com.example.thesstransit.ui.data.PlaceType
import com.example.thesstransit.ui.data.RecentSearchStorage
import com.example.thesstransit.ui.data.SavedLocations
import com.example.thesstransit.ui.location.LocationProvider
import com.example.thesstransit.ui.location.ReverseGeocoder
import com.example.thesstransit.ui.utils.SharedKeys
import com.example.thesstransit.ui.viewModels.TransitSearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: TransitSearchViewModel = viewModel()
) {

    val context = LocalContext.current

    val myLocationSting = stringResource(R.string.my_location)
    val currentLocationString = stringResource(R.string.current_location)

    var fromQuery by remember {
        mutableStateOf(myLocationSting)
    }

    var fromPlace by remember {
        mutableStateOf<Place?>(
            Place(
                name = myLocationSting,
                latitude = 0.0,
                longitude = 0.0,
                type = PlaceType.CURRENT_LOCATION
            )
        )
    }

    val locationProvider = remember {
        LocationProvider(context)
    }

    val reverseGeocoder = remember {
        ReverseGeocoder(context)
    }

    val savedLocations = remember {
        SavedLocations(context)
    }

    val home by savedLocations.home.collectAsState(
        initial = Triple(null, null, null)
    )

    val work by savedLocations.work.collectAsState(
        initial = Triple(null, null, null)
    )

    var toPlace by remember {
        mutableStateOf<Place?>(null)
    }

    var destinationQuery by remember {
        mutableStateOf("")
    }

    val destinationFocus = remember {
        FocusRequester()
    }

    val scope = rememberCoroutineScope()

    val keyboard = LocalSoftwareKeyboardController.current

    var results by remember {
        mutableStateOf<List<Place>>( emptyList() )
    }

    val storage = remember { RecentSearchStorage(context) }

    val recentSearches by storage.searches.collectAsState(
        initial = emptyList()
    )

    var showContent by remember {
        mutableStateOf(false)
    }

    var locationPressed by remember {
        mutableStateOf(false)
    }

    val locationScale by animateFloatAsState(
        targetValue =
            if(locationPressed) 0.9f else 1f,
        animationSpec = spring()
    )

    var swapRotation by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(Unit) {
        showContent = true
    }

    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn( animationSpec = tween(220) ) +
        slideInVertically( initialOffsetY = {it/8}, animationSpec = tween(260) )
    ) {
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
        ) {

            with(sharedTransitionScope) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SharedKeys.SEARCH_BAR
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                ) {
                    /*

                    ScreenHeader(

                    )

                     */
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Column(modifier = Modifier.animateContentSize()) {
                            SearchField(
                                stringResource(R.string.search_from_label),
                                value = fromQuery,
                                onValueChange = {
                                    fromQuery = it
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            SearchField(
                                title = stringResource(R.string.search_to_label),
                                focusRequester = destinationFocus,
                                value = destinationQuery,
                                onValueChange = { query ->
                                    destinationQuery = query

                                    viewModel.search(query) { searchResults ->
                                        results = searchResults.map {
                                            Place(
                                                name = it.title,
                                                latitude = it.latitude,
                                                longitude = it.longitude,
                                                type = PlaceType.SEARCH
                                            )
                                        }
                                    }
                                }
                            )

                            Spacer( modifier = Modifier.height(12.dp) )

                            Row {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                    tonalElevation = 6.dp,
                                    shadowElevation = 3.dp,
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                                    )
                                ) {
                                    IconButton(
                                        onClick = {
                                            swapRotation += 180f

                                            val tempQuery = fromQuery
                                            fromQuery = destinationQuery
                                            destinationQuery = tempQuery

                                            val tempPlace = fromPlace
                                            fromPlace = toPlace
                                            toPlace = tempPlace
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.SwapVerticalCircle,
                                            null,
                                            modifier = Modifier.graphicsLayer {
                                                rotationZ = swapRotation
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                    tonalElevation = 6.dp,
                                    shadowElevation = 3.dp,
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier
                                        .graphicsLayer {
                                            scaleX = locationScale
                                            scaleY = locationScale
                                        }
                                        .clickable {
                                            locationPressed = true
                                            scope.launch {
                                                delay(100.milliseconds)
                                                locationPressed = false
                                            }
                                        }
                                ) {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                val location = locationProvider.getCurrentLocation()

                                                location?.let {
                                                    val name =
                                                        reverseGeocoder.getName(
                                                            it.latitude,
                                                            it.longitude
                                                        ) ?: currentLocationString

                                                    fromPlace =
                                                        Place(
                                                            name = name,
                                                            latitude = it.latitude,
                                                            longitude = it.longitude,
                                                            type = PlaceType.CURRENT_LOCATION
                                                        )

                                                    fromQuery = name
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.MyLocation,
                                            null
                                        )
                                    }
                                }

                            }

                            LaunchedEffect(destinationQuery) {

                                if (destinationQuery.isBlank()) {
                                    results = emptyList()
                                }

                            }

                            LaunchedEffect(Unit) {
                                delay(250.milliseconds)
                                destinationFocus.requestFocus()
                                delay(300.milliseconds)
                                keyboard?.show()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Construction,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {

                                Text(
                                    text = "UNDER CONSTRUCTION",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Trip planning is still under development. Search results are available, but route calculation and navigation will be added in a future update.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    SectionTitle(
                        title = stringResource(R.string.recent_searches_title)
                    )

                    AnimatedVisibility(
                        visible = results.isEmpty(),
                        enter = fadeIn()
                    ) {
                        Column {
                            recentSearches.forEach { search ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    tonalElevation = 2.dp
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text(search.title)
                                        },
                                        supportingContent = {
                                            Text( stringResource(R.string.recent_search_subtitle) )
                                        },
                                        leadingContent = {
                                            Icon(
                                                Icons.Outlined.LocationOn,
                                                null
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            destinationQuery = search.title

                                            viewModel.search(search.title) { searchResults ->
                                                results = searchResults.map {
                                                    Place (
                                                        name = it.title,
                                                        latitude = it.latitude,
                                                        longitude = it.longitude,
                                                        type = PlaceType.SEARCH
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }

                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = results.isNotEmpty(),
                        enter = fadeIn() + scaleIn(initialScale = 0.95f) + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        var cardVisible by remember {
                            mutableStateOf(false)
                        }

                        LaunchedEffect(results) {
                            cardVisible = results.isNotEmpty()
                        }

                        val elevation by animateDpAsState(
                            targetValue =
                                if (cardVisible)
                                    8.dp
                                else
                                    0.dp,
                            animationSpec = spring()
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = 0.8f,
                                        stiffness = 300f
                                    )
                                ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = elevation
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {

                                itemsIndexed(results) { index, item ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter =
                                            fadeIn(
                                                animationSpec = tween(
                                                    durationMillis = 250,
                                                    delayMillis = index * 40
                                                )
                                            )
                                                    +
                                                    scaleIn(
                                                        initialScale = 0.92f,
                                                        animationSpec = spring(
                                                            dampingRatio = 0.75f,
                                                            stiffness = 350f
                                                        )
                                                    )
                                                    +
                                                    slideInVertically(
                                                        animationSpec = tween(
                                                            durationMillis = 260,
                                                            delayMillis = index * 40
                                                        )
                                                    )
                                    ) {

                                        ListItem(
                                            leadingContent = {
                                                Icon(
                                                    Icons.Outlined.LocationOn,
                                                    null
                                                )
                                            },

                                            headlineContent = {
                                                Text(
                                                    item.name.substringBefore(",")
                                                )
                                            },

                                            supportingContent = {
                                                Text(
                                                    item.name.substringAfter(",", "")
                                                )
                                            },

                                            modifier = Modifier.clickable {
                                                destinationQuery = item.name
                                                toPlace = Place(
                                                    name = item.name,
                                                    latitude = item.latitude,
                                                    longitude = item.longitude,
                                                    type = PlaceType.SEARCH
                                                )
                                                results = emptyList()

                                                scope.launch {

                                                    storage.saveSearch(
                                                        title = item.name,
                                                        latitude = item.latitude,
                                                        longitude = item.longitude
                                                    )

                                                }
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
