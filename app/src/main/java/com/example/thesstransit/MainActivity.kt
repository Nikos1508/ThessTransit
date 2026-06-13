package com.example.thesstransit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.thesstransit.ui.item.HomeScreen
import com.example.thesstransit.ui.components.ThessTransitBottomBar
import com.example.thesstransit.ui.theme.ThessTransitTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThessTransitTheme(darkTheme = true) {

                var selectedTab by remember { mutableStateOf(1) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ThessTransitBottomBar(
                            selectedIndex = selectedTab,
                            onItemSelected = { newIndex ->
                                selectedTab = newIndex
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        when (selectedTab) {
                            0 -> {

                            }
                            1 -> {
                                HomeScreen(
                                    onLoginClick = { selectedTab = 2 },
                                    onSearchClick = { /* TODO */ },
                                    onHowToGoClick = { /* TODO */ },
                                    onLinesClick = { /* TODO */ },
                                    onNearbyStopsClick = { /* TODO */ },
                                    onLiveDeparturesClick = { /* TODO */ }
                                )
                            }
                            2 -> {

                            }
                        }
                    }
                }
            }
        }
    }
}