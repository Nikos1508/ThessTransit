package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.AppTheme
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import com.example.thesstransit.ui.viewModels.ThemeViewModel
import io.gitlab.mitsiosm.oseth.data.Language
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: LanguageViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {

    val language by viewModel.language.collectAsState()
    val theme by themeViewModel.theme.collectAsState()

    var showAbout by remember { mutableStateOf(false) }

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        visible = true
    }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically {
                -it / 3
            }
        ) {
            SettingsContent(
                language = language,
                languageViewModel = viewModel,
                theme = theme,
                themeViewModel = themeViewModel,
                showAbout = {
                    showAbout = true
                },
                onBackClick = onBackClick
            )
        }

        if(showAbout) {
            AlertDialog(
                onDismissRequest = {
                    showAbout = false
                },

                title = {
                    Text("ThessTransit")
                },

                text = {
                    Text(
                        """
                        Δημιουργήθηκε για μία εύκολη
                        μετακίνηση στη Θεσσαλονίκη.
                        
                        Version 0.8
                        """
                    )
                },

                confirmButton = {
                    TextButton(
                        onClick = { showAbout = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsContent(
    language: Language,
    languageViewModel: LanguageViewModel,
    theme: AppTheme,
    themeViewModel: ThemeViewModel,
    showAbout: () -> Unit,
    onBackClick: () -> Unit
) {

    var step by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        repeat(6) {
            delay(200.milliseconds)
            step++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenHeader(
            title = "Ρυθμίσεις",
            onBackClick = onBackClick,
            onProfileClick = {}
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                AnimatedVisibility(
                    visible = step >= 1,
                    enter = fadeIn() + slideInVertically()
                ) {
                    SettingsHeader()
                }
            }

            item {
                SectionTitle( "Γενικά" )
            }

            item {
                AnimatedVisibility(
                    visible = step >= 2,
                    enter = fadeIn() + slideInVertically()
                ) {
                    SettingsCard(
                        icon = Icons.Default.Language,
                        title = "Γλώσσα",
                        subtitle =
                            if (language == Language.GREEK)
                                "Ελληνικά"
                            else
                                "English",
                        trailing = {
                            Switch(
                                checked = language == Language.ENGLISH,
                                onCheckedChange = { }
                            )
                        }
                    )
                }
            }

            item {
                SectionTitle( "Εμφάνιση" )
            }

            item {
                AnimatedVisibility(
                    visible = step >= 3,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ThemeCard(
                        selected = theme,
                        onThemeSelected = {
                            themeViewModel.setTheme(it)
                        }
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = step >= 4,
                    enter = fadeIn() + slideInVertically()
                ) {
                    SettingsCard(
                        icon = Icons.Default.Call,
                        title = "Στοιχεία επικοινωνίας",
                        subtitle = "Επικοινωνήστε μαζί μας"
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn() + slideInVertically()
                ) {
                    SettingsCard(
                        icon = Icons.Default.Info,
                        title = "Σχετικά",
                        subtitle = "'Εκδοση 0.8",
                        onClick = showAbout
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader() {

    val transition = rememberInfiniteTransition(label = "settingsGlow")

    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(
                3000,
                easing = FastOutSlowInEasing
            ),
            RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(
                10.dp,
                RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer{
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Ρυθμίσεις",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Προσωποποίησε την εμπειρία σου στο ThessTransit",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =18.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        ListItem (
            leadingContent = {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.13f))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },

            headlineContent = {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold
                )
            },

            supportingContent = {
                Text(
                    subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },

            trailingContent = {
                trailing?.invoke()
            }
        )
    }
}

@Composable
private fun ThemeCard(
    selected: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(
                5.dp,
                RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.82f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        Column( modifier = Modifier.padding(20.dp) ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background( color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) )
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer( modifier = Modifier.width(14.dp) )

                Column {
                    Text(
                        "Θέμα εφαρμογής",
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Διάλεξε εμφάνιση",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer( modifier = Modifier.height(20.dp) )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeOption(
                    text = "Συστήματος",
                    selected = selected == AppTheme.SYSTEM
                ) {
                    onThemeSelected(
                        AppTheme.SYSTEM
                    )
                }

                ThemeOption(
                    text = "Φωτεινό",
                    selected = selected == AppTheme.LIGHT
                ) {
                    onThemeSelected(
                        AppTheme.LIGHT
                    )
                }

                ThemeOption(
                    text = "Σκούρο",
                    selected = selected == AppTheme.DARK
                ) {
                    onThemeSelected(
                        AppTheme.DARK
                    )
                }

            }
        }
    }

}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue =
            if (selected) 1.05f else 1f,
        label = "themeScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(color =
                if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent
            )
            .clickable(
                onClick = onClick
            )
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color =
                if (selected)
                    Color.White
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Normal
        )
    }
}

@Composable
private fun SectionTitle(
    text:String
){
    Text(
        text = text,
        modifier = Modifier
            .padding(
                horizontal = 22.dp,
                vertical = 4.dp
            ),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}
