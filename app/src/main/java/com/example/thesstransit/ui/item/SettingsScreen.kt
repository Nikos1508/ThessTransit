package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.LanguageViewModel
import io.gitlab.mitsiosm.oseth.data.Language

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: LanguageViewModel = viewModel()
) {

    val language by viewModel.language.collectAsState()

    Column( modifier = Modifier.fillMaxSize() ) {
        ScreenHeader(
            title = "Ρυθμίσεις",
            onBackClick = onBackClick,
            onProfileClick = {}
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text("Γλώσσα")
                    },

                    supportingContent = {
                        Text(
                            if (language == Language.GREEK)
                                "Ελληνικά"
                            else
                                "English"
                        )
                    },

                    leadingContent = {
                        Icon(
                            Icons.Default.Language,
                            null
                        )
                    },

                    trailingContent = {
                        Switch(
                            checked = language == Language.ENGLISH,
                            onCheckedChange = {
                                viewModel.toggleLanguage()
                            }
                        )
                    }
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = {
                        Text("Θέμα")
                    },

                    supportingContent = {
                        Text("Σύντομα διαθέσιμο")
                    },

                    leadingContent = {
                        Icon(Icons.Default.Palette, null)
                    }
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Ειδοποιήσεις")
                    },

                    supportingContent = {
                        Text("Σύντομα διαθέσιμο")
                    },

                    leadingContent = {
                        Icon(
                            Icons.Default.Notifications,
                            null
                        )
                    }
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = {
                        Text("Σχετικά με την εφαρμογή")
                    },

                    supportingContent = {
                        Text("Έκδοση 0.8")
                    },

                    leadingContent = {
                        Icon(
                            Icons.Default.Info,
                            null
                        )
                    }
                )
            }
        }
    }
}