package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.thesstransit.ui.components.SearchField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement

@Composable
fun SearchScreen(
    onBackClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        null
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    SearchField(
                        title = "Από",
                        value = "Η τοποθεσία μου"
                    )

                    Spacer( modifier = Modifier.height(12.dp) )

                    SearchField(
                        title = "Προς",
                        value = ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Πρόσφατες αναζητήσεις",
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Πρόσφατα",
                        fontWeight = FontWeight.Bold
                    )

                    listOf(
                        "Καμάρα",
                        "Λευκός πύργος",
                        "Νέα ελβετία"
                    )
                        .forEach {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                }

            }
        }

    }
}
