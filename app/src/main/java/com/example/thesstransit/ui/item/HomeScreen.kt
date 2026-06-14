package com.example.thesstransit.ui.item

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesstransit.R
import com.example.thesstransit.ui.theme.ThessTransitTheme

@Composable
fun HomeScreen(
    onLoginClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onHowToGoClick: () -> Unit = {},
    onLinesClick: () -> Unit = {},
    onNearbyStopsClick: () -> Unit = {},
    onLiveDeparturesClick: () -> Unit = {}
){
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
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                HeaderSection(onLoginClick = onLoginClick)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SearchBarSection(onSearchClick = onSearchClick)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuickAccessRow()
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                MainFeatureGrid(
                    onHowToGoClick,
                    onLinesClick,
                    onNearbyStopsClick,
                    onLiveDeparturesClick
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
                FeatureTilesGrid()
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
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
                    .fillMaxWidth(0.6f)
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
                        text = "Flow",
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
    onSearchClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(68.dp)
            .bounceClick { onSearchClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Πού θέλεις να πας;",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(32.dp)
                    .padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .bounceClick { },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class QuickCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun QuickAccessRow() {

    val items = listOf(
        QuickCard("Οικία", "25ης Μαρτίου, 17, Επανομή", Icons.Outlined.Home),
        QuickCard("Εργασία", "Καθόρισε", Icons.Outlined.Work),
        QuickCard("Αγαπημένα", "0 Διαδρομές", Icons.Outlined.Star)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(88.dp)
                    .bounceClick { },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = item.subtitle,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Πώς να πάω;",
                subtitle = "Βρες την καλύτερη διαδρομή",
                icon = Icons.Outlined.Route,
                onClick = onHowToGoClick
            )

            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Γραμμές & Δρομολόγια",
                subtitle = "Δες ολες τις γραμμές και τα δρομολόγια",
                icon = Icons.Outlined.DirectionsBus,
                onClick = onLinesClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Στάσεις κοντά μου",
                subtitle = "Στάσεις λεφορείου και μετρό κοντά σου",
                icon = Icons.Outlined.LocationOn,
                onClick = onNearbyStopsClick
            )

            MainFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Αναχωρήσεις τώρα",
                subtitle = "Ζωντανά δεδομένα αναχωρήσεων και αφίξεων",
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
            .height(180.dp)
            .bounceClick { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
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
fun FeatureTilesGrid() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        featureTiles.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                rowItems.forEach {
                    SmallFeatureTile(
                        modifier = Modifier.weight(1f),
                        tile = it
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
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
            .height(125.dp)
            .bounceClick {},
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ){
        Icon(
            imageVector = tile.icon,
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column {
            Text(
                text = tile.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tile.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

data class FeatureTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

val featureTiles = listOf(
    FeatureTile(
        "Εισιτήρια & Τιμές",
        "Τύποι εισιτηρίων,\nτιμές & οδηγίες",
        Icons.Outlined.LocalActivity
    ),

    FeatureTile(
        "Αγορά Εισιτηρίου",
        "Αγοράστε ψηφιακά\nεισιτήρια",  /* Αυτό πρέπει να το δουμε TODO */
        Icons.Outlined.QrCode2
    ),

    FeatureTile(
        "Κυκλοφορία",
        "Δες την κίνηση\nστους δρόμους",
        Icons.Outlined.DirectionsCar
    ),
    FeatureTile(
        "Μετρό",
        "Χάρτης, γραμμές\n& δρομολόγια",
        Icons.Outlined.Train
    ),

    FeatureTile(
        "Αγαπημένες\nΔιαδρομές",
        "Οι συχνότερες\nδιαδρομές σου",
        Icons.Outlined.Favorite
    ),

    FeatureTile(
        "Αποθηκευμένοι\nΧάρτες",
        "Κατέβασε χάρτες\nγια offline χρήση",
        Icons.Outlined.Map
    ),

    FeatureTile(
        "Ειδοποιήσεις",
        "Λάβε ειδοποιήσεις για\nγραμμές & διακοπές",
        Icons.Outlined.Notifications
    ),

    FeatureTile(
        "Ρυθμίσεις",
        "Γλώσσα, εμφάνιση\n& προτιμήσεις",
        Icons.Outlined.Settings
    )
)


fun Modifier.bounceClick(onClick: () -> Unit) = this.composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "BounceAnimation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
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

@Preview (showBackground = true)
@Composable
fun HomeScreenPreview() {
    ThessTransitTheme(darkTheme = true) {
        HomeScreen()
    }
}