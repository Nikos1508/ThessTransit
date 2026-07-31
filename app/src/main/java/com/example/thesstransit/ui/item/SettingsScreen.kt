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
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.AppTheme
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import com.example.thesstransit.ui.viewModels.ThemeViewModel
import com.example.thesstransit.ui.viewModels.TutorialViewModel
import io.gitlab.mitsiosm.oseth.data.Language
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import io.github.jan.supabase.auth.user.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: UserInfo?,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: LanguageViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    tutorialViewModel: TutorialViewModel
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
                user = user,
                language = language,
                languageViewModel = viewModel,
                theme = theme,
                themeViewModel = themeViewModel,
                showAbout = {
                    showAbout = true
                },
                onBackClick = onBackClick,
                onLoginClick = onLoginClick,
                onLogoutClick = onLogoutClick,
                tutorialViewModel = tutorialViewModel
            )
        }

        if(showAbout) {
            AlertDialog(
                onDismissRequest = {
                    showAbout = false
                },

                title = {
                    Text(
                        "ThessTransit"
                    )
                },

                text = {
                    Text(
                        stringResource(R.string.settings_about_dialog_text),
                        textAlign = TextAlign.Center
                    )
                },

                confirmButton = {
                    TextButton(
                        onClick = { showAbout = false }
                    ) {
                        Text( stringResource(R.string.ok) )
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsContent(
    user: UserInfo?,
    language: Language,
    languageViewModel: LanguageViewModel,
    theme: AppTheme,
    themeViewModel: ThemeViewModel,
    showAbout: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    tutorialViewModel: TutorialViewModel
) {

    var step by remember {
        androidx.compose.runtime.mutableIntStateOf(0)
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
            title = stringResource(R.string.title_settings),
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
                AccountCard(
                    user = user,
                    onLoginClick = onLoginClick,
                    onLogoutClick = onLogoutClick
                )
            }

            item {
                SectionTitle( stringResource(R.string.section_general) )
            }

            item {
                AnimatedVisibility(
                    visible = step >= 2,
                    enter = fadeIn() + slideInVertically()
                ) {
                    SettingsCard(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.settings_language),
                        subtitle =
                            if (language == Language.GREEK)
                                stringResource(R.string.greek)
                            else
                                stringResource(R.string.english),
                        trailing = {
                            Switch(
                                checked = language == Language.ENGLISH,
                                onCheckedChange = { languageViewModel.toggleLanguage() }
                            )
                        }
                    )
                }
            }

            item {
                SectionTitle( stringResource(R.string.section_appearance) )
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
                        title = stringResource(R.string.settings_contact_title),
                        subtitle = stringResource(R.string.settings_contact_subtitle)
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
                        title = stringResource(R.string.settings_about_title),
                        subtitle = stringResource(R.string.settings_about_subtitle),
                        onClick = showAbout
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = step >= 6,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ListItem(
                        headlineContent = {
                            Text("Replay tutorial")
                        },
                        supportingContent = {
                            Text(
                                "Show the ThessTransit introduction again"
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Outlined.School,
                                null
                            )
                        },
                        modifier = Modifier.clickable {
                            tutorialViewModel.replayTutorial()
                        }

                    )
                }
            }

            item {
                Spacer( modifier = Modifier.height(12.dp) )
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
                .padding(vertical = 28.dp, horizontal = 12.dp),
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
                text = stringResource(R.string.title_settings),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.settings_header_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountCard(
    user: UserInfo?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    SettingsCard(
        icon =
            if (user != null)
                Icons.Default.Person
            else
                Icons.Default.Login,
        title =
            if(user!=null)
                "Συνδεδεμένος"
            else
                "Σύνδεση",

        subtitle =
            if (user != null)
                user.email ?: "Λογαριασμός ThessTransit"
            else
                "Συνδεθείτε για αποθήκευση προτιμήσεων",

        onClick =
            if (user != null)
                onLogoutClick
            else
                onLoginClick

        )
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
                        stringResource(R.string.theme_title),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        stringResource(R.string.theme_subtitle),
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
                    text = stringResource(R.string.theme_system),
                    selected = selected == AppTheme.SYSTEM
                ) {
                    onThemeSelected(
                        AppTheme.SYSTEM
                    )
                }

                ThemeOption(
                    text = stringResource(R.string.theme_light),
                    selected = selected == AppTheme.LIGHT
                ) {
                    onThemeSelected(
                        AppTheme.LIGHT
                    )
                }

                ThemeOption(
                    text = stringResource(R.string.theme_dark),
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
