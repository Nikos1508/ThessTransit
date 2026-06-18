package com.example.thesstransit.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesstransit.ui.components.ScreenHeader

data class MockBusLine(
    val number: String,
    val name: String,
)

val mockRoutes = listOf(
    MockBusLine("01X", "ΚΤΕΛ - Αεροδρόμιο (Express)"),
    MockBusLine("03K", "Ν.Σ.Σ. - ΑΣ ΙΚΕΑ"),
    MockBusLine("01X", "ΚΤΕΛ - Αεροδρόμιο (Express)"),
    MockBusLine("02K", "Παράδειγμα - Test"),
    MockBusLine("01X", "ΚΤΕΛ - Αεροδρόμιο (Express)"),
    MockBusLine("69A", "ΑΣ ΙΚΕΑ - Παραλία Επανομής")
)

@Composable
fun RoutesScreen(
    onBackClick: () -> Unit
) {
    Column {
        ScreenHeader(title = "Γραμμές", onBackClick = onBackClick)
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
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 12.dp
                    )
                ) {
                    Text(
                        text = "Διαδρομές",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Δείτε τις γραμμές και τα δρομολόγια των λεωφορείων",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    items(mockRoutes) { route ->
                        BusRouteRowItem(route = route)
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun BusRouteRowItem(route: MockBusLine){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsBus,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = route.number,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = route.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Keimeno poy ua htan h perigrafh",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}