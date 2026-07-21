package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.AnimatedSearchBar
import com.example.thesstransit.ui.components.RouteFiltersDialog
import com.example.thesstransit.ui.data.SavedLocations
import com.example.thesstransit.ui.utils.SharedKeys
import com.example.thesstransit.ui.viewModels.FavoritesViewModel
import io.gitlab.mitsiosm.oseth.Oseth

@OptIn(ExperimentalSharedTransitionApi::class)
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
    onWorkClick: () -> Unit = {},
    favoritesViewModel: FavoritesViewModel = viewModel(),

    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
){

    val tiles = listOf(
        FeatureTile("Εισιτήρια\n& Τιμές", "", Icons.Outlined.LocalActivity, onTicketsClick),
        FeatureTile("Αγορά\nΕισιτηρίου", "", Icons.Outlined.QrCode2, onBuyTicketClick),
        FeatureTile("Γραμμές\nΜετρό", "", Icons.Outlined.Train, onLinesClick),
        FeatureTile("Αγαπημένες\nΔιαδρομές", "", Icons.Outlined.Favorite, onFavouritesClick),
        FeatureTile("Ειδοποιήσεις", "", Icons.Outlined.Notifications, onNotificationsClick),
        FeatureTile("Ρυθμίσεις", "", Icons.Outlined.Settings, onSettingsClick)
    )

    var showFilters by remember { mutableStateOf(false) }

    val favoriteRoutes by favoritesViewModel.favorites.collectAsState()
    val favoriteGroups by favoritesViewModel.favoriteGroups.collectAsState()
    val totalFavorites = favoriteRoutes.size + favoriteGroups.size

    val context = LocalContext.current
    val api = Oseth(context)

    LaunchedEffect(api) {
        api.sync(true)
    }

    val savedLocations = remember(context) {
        SavedLocations(context)
    }

    val home by savedLocations.home.collectAsState(
        initial = Triple(null, null, null)
    )

    val work by savedLocations.work.collectAsState(
        initial = Triple(null, null, null)
    )

    var searchPressed by remember {
        mutableStateOf(false)
    }

    val searchScale by animateFloatAsState(
        targetValue =
            if (searchPressed)
                0.96f
            else
                1f,
        animationSpec = spring()
    )

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
                HeaderSection(onLoginClick = onLoginClick)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                with(sharedTransitionScope){
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
            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuickAccessRow(
                    favoriteCount = totalFavorites,
                    onHomeClick = onHomeClick,
                    onWorkClick = onWorkClick,
                    onFavouritesClick = onFavouritesClick,
                    homeSubtitle = home.first ?: "Καθόρισε",
                    workSubtitle = work.first ?: "Καθόρισε"
                )
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                MainFeatureGrid(
                    onHowToGoClick = onHowToGoClick,
                    onLinesClick = onLinesClick,
                    onNearbyStopsClick = onNearbyStopsClick,
                    onLiveDeparturesClick = onLiveDeparturesClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Ενημερώσεις / Νέα"
                )
            }
            item {
                AIUpdateSection()
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Επιπλέον λειτουργίες"
                )
            }
            item {
                FeatureTilesGrid(tilesList = tiles)
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
    }
}

@Composable
fun HeaderSection(
    onLoginClick: () -> Unit = {}
) {
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
            OutlinedButton(
                onClick = onLoginClick,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Login",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Καλωσόρισες στο",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thess",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Transit",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Η έξυπνη μετακίνηση στη Θεσσαλονίκη",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SearchBarSection(
    onFilterClick: () -> Unit,
    onSearchClick: () -> Unit
) {

    var searchQuery by remember {mutableStateOf("")}

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(58.dp)
            .bounceClick( onClick = onSearchClick ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = { searchQuery = "" },
                modifier = Modifier.padding(start = 8.dp)
            ){
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search Button",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Πού θέλεις να πας;",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }

                BasicTextField(
                    value = searchQuery,
                    onValueChange = {searchQuery = it},
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { searchQuery }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(28.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .bounceClick { onFilterClick() },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Φίλτρα Αναζήτησης",
                    tint = MaterialTheme.colorScheme.onSurface
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
    workSubtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAccessItem(
            title = "Οικία",
            subtitle = homeSubtitle,
            icon = Icons.Outlined.Home,
            iconColor = Color.White,
            onClick = onHomeClick,
            modifier = Modifier.weight(1f)
        )

        QuickAccessItem(
            title = "Εργασία",
            subtitle = workSubtitle,
            icon = Icons.Outlined.Work,
            iconColor = MaterialTheme.colorScheme.primary,
            onClick = onWorkClick,
            modifier = Modifier.weight(1f)
        )

        QuickAccessItem(
            title = "Αγαπημένα",
            subtitle = "$favoriteCount διαδρομές",
            icon = Icons.Outlined.Star,
            iconColor = Color(0xFFFFD700),
            onClick = onFavouritesClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickAccessItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(82.dp).bounceClick { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(6.dp),
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
    onLiveDeparturesClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Πώς να πάω;",
                subtitle = "Βρες την καλύτερη διαδρομή",
                icon = Icons.Outlined.Route,
                onClick = onHowToGoClick
            )
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Γραμμές &\nΔρομολόγια",
                subtitle = "Δες όλες τις γραμμές και τα δρομολόγια",
                icon = Icons.Outlined.DirectionsBus,
                onClick = onLinesClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Στάσεις κοντά\nμου",
                subtitle = "Στάσεις λεωφορείου και μετρό κοντά σου",
                icon = Icons.Outlined.LocationOn,
                onClick = onNearbyStopsClick
            )
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Αναχωρήσεις\nτώρα",
                subtitle = "Ζωντανά δεδομένα των αναχωρήσεων και αφίξεων λεωφορείων και μετρό",
                icon = Icons.Outlined.AccessTime,
                onClick = onLiveDeparturesClick
            )
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
fun SectionTitle(
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

val aiUpdates = listOf(

    AIUpdate(
        title = "Κυκλοφορία",
        description = "Αυξημένη κίνηση στην Εγνατία Οδό",
        icon = Icons.Outlined.DirectionsCar
    ),

    AIUpdate(
        title = "Έργα & Διακοπές",
        description = "Γραμμή 3K: Καθυστέρηση 10 λεπτών",
        icon = Icons.Outlined.Notifications
    )

)

@Composable
fun  AIUpdateSection(){
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        aiUpdates.forEach {
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
fun FeatureTilesGrid(tilesList: List<FeatureTile>) {

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
                        tile = tile
                    )
                }
            }
        }
    }
}

@Composable
fun SmallFeatureTile(
    modifier: Modifier = Modifier,
    tile: FeatureTile
) {
    Surface(
        modifier = modifier
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