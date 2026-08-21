package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.SwapVerticalCircle
import androidx.compose.material3.Button
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
    onFindRouteClick: (Place, Place) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: TransitSearchViewModel = viewModel()
) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    val myLocationString = stringResource(R.string.my_location)

    val currentLocationString = stringResource(R.string.current_location)

    var fromPlace by remember {
        mutableStateOf<Place?>(null)
    }

    var fromQuery by remember {
        mutableStateOf(myLocationString)
    }

    var searchingFrom by remember {
        mutableStateOf(false)
    }

    var toPlace by remember {
        mutableStateOf<Place?>(null)
    }

    var destinationQuery by remember {
        mutableStateOf("")
    }

    var results by remember {
        mutableStateOf<List<Place>>(emptyList())
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var latestSearchQuery by remember {
        mutableStateOf("")
    }

    val destinationFocus = remember {
        FocusRequester()
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

    val storage = remember {
        RecentSearchStorage(context)
    }

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
        targetValue = if (locationPressed) 0.9f else 1f,
        animationSpec = spring(),
        label = "locationScale"
    )

    var swapRotation by remember {
        mutableFloatStateOf(0f)
    }


    fun selectPlace(place: Place) {

        if (searchingFrom) {
            fromPlace = place
            fromQuery = place.name
        } else {
            toPlace = place
            destinationQuery = place.name
        }

        results = emptyList()
        isSearching = false

        keyboard?.hide()
    }

    fun performSearch(
        query: String,
        from: Boolean
    ) {

        val cleanQuery = query.trim()

        if (cleanQuery.length < 2) {
            results = emptyList()
            isSearching = false
            return
        }

        latestSearchQuery = cleanQuery
        isSearching = true

        viewModel.search(cleanQuery) { searchResults ->

            if (latestSearchQuery != cleanQuery) {
                return@search
            }

            results = searchResults.map { result ->

                Place(
                    name = result.title,
                    latitude = result.latitude,
                    longitude = result.longitude,
                    type = PlaceType.SEARCH
                )
            }

            isSearching = false
        }
    }

    LaunchedEffect(Unit) {
        showContent = true
        delay(duration = 250.milliseconds)

        destinationFocus.requestFocus()
        delay(duration = 300.milliseconds)

        keyboard?.show()
    }

    AnimatedVisibility(
        visible = showContent,
        enter =
            fadeIn(
                animationSpec = tween(durationMillis = 220)
            ) +
                    slideInVertically(
                        initialOffsetY = { it / 8 },
                        animationSpec = tween(durationMillis = 260)
                    )
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
                        .fillMaxSize()
                        .verticalScroll( rememberScrollState() )
                        .padding(16.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SharedKeys.SEARCH_BAR
                            ),
                            animatedVisibilityScope = animatedContentScope
                        )
                ) {
                    SearchField(
                        title = stringResource(R.string.search_from_label),
                        value = fromQuery,

                        onValueChange = { query ->
                            val actualQuery =
                                if (
                                    fromQuery == myLocationString &&
                                    query.startsWith(myLocationString)
                                ) {
                                    query.removePrefix(myLocationString)
                                } else {
                                    query
                                }

                            fromQuery = actualQuery
                            searchingFrom = true

                            fromPlace = null

                            performSearch(
                                query = actualQuery,
                                from = true
                            )
                        }
                    )

                    Spacer( modifier = Modifier.height(10.dp) )

                    SearchField(
                        title = stringResource(R.string.search_to_label),
                        focusRequester = destinationFocus,
                        value = destinationQuery,

                        onValueChange = { query ->
                            destinationQuery = query
                            searchingFrom = false
                            toPlace = null

                            performSearch(
                                query = query,
                                from = false
                            )
                        }
                    )

                    Spacer( modifier = Modifier.height(12.dp) )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                            tonalElevation = 6.dp,
                            shadowElevation = 3.dp,
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                            )
                        ) {

                            IconButton(
                                onClick = {
                                    swapRotation += 180f

                                    val oldFromQuery = fromQuery
                                    val oldToQuery = destinationQuery
                                    val oldFromPlace = fromPlace
                                    val oldToPlace = toPlace

                                    fromQuery = oldToQuery.ifBlank { myLocationString }
                                    destinationQuery = oldFromQuery

                                    fromPlace = oldToPlace
                                    toPlace = oldFromPlace

                                    results = emptyList()
                                    searchingFrom = false
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.SwapVerticalCircle,
                                    contentDescription = "Αντιμετάθεση",
                                    modifier =
                                        Modifier.graphicsLayer {
                                            rotationZ = swapRotation
                                        }
                                )
                            }
                        }

                        Spacer( modifier = Modifier.width(10.dp) )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color =
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                            tonalElevation = 6.dp,
                            shadowElevation = 3.dp,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.secondary
                                    .copy(alpha = 0.25f)
                            ),
                            modifier =
                                Modifier.graphicsLayer {
                                    scaleX = locationScale
                                    scaleY = locationScale
                                }
                        ) {
                            IconButton(
                                onClick = {
                                    locationPressed = true

                                    scope.launch {
                                        delay(100.milliseconds)
                                        locationPressed = false
                                        val location = locationProvider.getCurrentLocation()

                                        location?.let {

                                            val name =
                                                reverseGeocoder
                                                    .getName(
                                                        it.latitude,
                                                        it.longitude
                                                    )
                                                    ?: currentLocationString

                                            fromPlace =
                                                Place(
                                                    name = name,
                                                    latitude = it.latitude,
                                                    longitude = it.longitude,
                                                    type = PlaceType.CURRENT_LOCATION
                                                )

                                            fromQuery = name
                                            searchingFrom = true

                                            results = emptyList()
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MyLocation,
                                    contentDescription = "Τρέχουσα τοποθεσία"
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isSearching || results.isNotEmpty(),
                        enter = fadeIn() + scaleIn(initialScale = 0.95f),
                        exit = fadeOut()
                    ) {

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
                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 8.dp
                                ),
                            shape =
                                RoundedCornerShape(20.dp)
                        ) {

                            if (isSearching) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = "Αναζήτηση τοποθεσιών..."
                                    )
                                }

                            } else {
                                LazyColumn(
                                    modifier =
                                        Modifier.heightIn(
                                            max = 320.dp
                                        )
                                ) {
                                    itemsIndexed(
                                        results
                                    ) { index, place ->

                                        AnimatedVisibility(
                                            visible = true,
                                            enter =
                                                fadeIn(
                                                    animationSpec =
                                                        tween(
                                                            durationMillis = 250,
                                                            delayMillis = index * 40
                                                        )
                                                ) +
                                                        scaleIn(
                                                            initialScale = 0.92f,
                                                            animationSpec =
                                                                spring(
                                                                    dampingRatio = 0.75f,
                                                                    stiffness = 350f
                                                                )
                                                        ) +
                                                        slideInVertically(
                                                            animationSpec =
                                                                tween(
                                                                    durationMillis = 260,
                                                                    delayMillis = index * 40
                                                                )
                                                        )
                                        ) {
                                            ListItem(
                                                leadingContent = {
                                                    Icon(
                                                        imageVector = Icons.Outlined.LocationOn,
                                                        contentDescription = null
                                                    )
                                                },

                                                headlineContent = {
                                                    Text(
                                                        text =
                                                            place.name
                                                                .substringBefore(
                                                                    ","
                                                                ),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                },

                                                supportingContent = {
                                                    val address =
                                                        place.name
                                                            .substringAfter(
                                                                ",",
                                                                ""
                                                            )
                                                            .trim()

                                                    if (address.isNotEmpty()) {
                                                        Text(text = address)
                                                    }
                                                },

                                                modifier =
                                                    Modifier.clickable {
                                                        selectPlace(place)

                                                        scope.launch {
                                                            storage.saveSearch(
                                                                title = place.name,
                                                                latitude = place.latitude,
                                                                longitude = place.longitude
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

                    AnimatedVisibility(
                        visible = fromPlace != null && toPlace != null,
                        enter = fadeIn() + scaleIn(initialScale = 0.95f),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = {
                                val from = fromPlace ?: return@Button
                                val to = toPlace ?: return@Button

                                keyboard?.hide()
                                onFindRouteClick(from, to)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp
                                ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Βρες διαδρομή",
                                modifier = Modifier.padding( vertical = 4.dp ),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible =
                            results.isEmpty() &&
                                    !isSearching,
                        enter = fadeIn()
                    ) {
                        Column {
                            Spacer( modifier = Modifier.height(16.dp) )

                            SectionTitle(
                                title = stringResource(R.string.recent_searches_title)
                            )

                            recentSearches.forEach { search ->

                                Surface(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding( vertical = 4.dp ),
                                    shape = RoundedCornerShape(16.dp),
                                    tonalElevation = 2.dp
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text( text = search.title )
                                        },

                                        supportingContent = {

                                            Text(
                                                text = stringResource(R.string.recent_search_subtitle)
                                            )
                                        },

                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null
                                            )
                                        },

                                        modifier =
                                            Modifier.clickable {

                                                val place =
                                                    Place(
                                                        name = search.title,
                                                        latitude = search.latitude,
                                                        longitude = search.longitude,
                                                        type = PlaceType.SEARCH
                                                    )

                                                selectPlace(place)
                                            }
                                    )
                                }
                            }
                        }
                    }

                    Spacer( modifier = Modifier.height(24.dp) )
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
        modifier =
            Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color =
            MaterialTheme.colorScheme.onSurface
    )
}