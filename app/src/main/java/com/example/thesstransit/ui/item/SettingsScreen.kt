package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

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
                        visible = true,
                        enter = fadeIn() + slideInVertically {
                            -it / 3
                        }
                    ) {
                        SettingsHeader()
                    }

                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                item {

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
                                onCheckedChange = { viewModel.toggleLanguage() }
                            )
                        }
                    )

                }

                item {

                    ThemeCard(
                        selected = theme,
                        onThemeSelected = {
                            themeViewModel.setTheme(it)
                        }
                    )

                }

                item {

                    SettingsCard(
                        icon = Icons.Default.Call,
                        title = "Στοιχεία επικοινωνίας",
                        subtitle = "Επικοινωνήστε μαζί μας"
                    )

                }

                item {

                    SettingsCard(
                        icon = Icons.Default.Info,
                        title = "Σχετικά",
                        subtitle = "'Εκδοση 0.8",
                        onClick = {
                            showAbout = true
                        }
                    )

                }

                item {
                    Spacer( modifier = Modifier.height(30.dp) )
                }
            }
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
private fun SettingsHeader() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
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
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(0.15f)
                    )
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
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
            .padding(horizontal = 18.dp)
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.78f)
        )
    ) {
        Column( modifier = Modifier.padding(18.dp) ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    Icons.Default.Palette,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer( modifier = Modifier.width(14.dp) )

                Text(
                    "Θέμα εφαρμογής",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer( modifier = Modifier.height(16.dp) )

            Row( horizontalArrangement = Arrangement.spacedBy(8.dp) ) {
                AppTheme.values().forEach { option ->

                    FilterChip(
                        selected = selected == option,
                        onClick = {
                            onThemeSelected(option)
                        },
                        label = {
                            Text(
                                when(option) {
                                    AppTheme.SYSTEM -> "Σύστημα"
                                    AppTheme.LIGHT -> "Φωτεινό"
                                    AppTheme.DARK -> "Σκούρο"
                                }
                            )
                        }
                    )

                }
            }
        }
    }

}