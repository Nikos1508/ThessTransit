package com.example.thesstransit.ui.item

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TicketsScreen() {

    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Τιμές & Είδη", "Εκπτώσεις", "Πού αγοράζω εισητήρια", "Πρόστημα")

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
        Column(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)) {
                Text(
                    text = "Εισητήρια",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Πληροφορίες για τη μετακίνησή σου στη Θεσσαλονίκη",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                when (selectedTab) {
                    0 -> pricesTabSection()
                    1 -> discountsTabSection()
                    2 -> locationsTabSection()
                    3 -> penaltiesTabSection()
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

fun LazyColumn.pricesTabSection() {
    item {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Ενιαίο Σύστημα (Λεωφορεία & Μετρό)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Επικεφαλίδα Πίνακα
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            .padding(14.dp)
                    ) {
                        Text("Τύπος Εισιτηρίου", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Κανονικό", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End, fontSize = 14.sp)
                        Text("Μειωμένο", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End, fontSize = 14.sp)
                    }

                    // Δεδομένα Πίνακα
                    TableRow("Αστικής ζώνης (1 μετακίνηση)", "0,60€", "0,30€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("Περιαστικής ζώνης", "0,80€", "0,40€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("Αεροδρόμιο / Εξπρές (1Χ)", "1,00€", "0,50€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("70 λεπτών (έως 2 μετακινήσεις)", "0,70€", "0,35€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("90 λεπτών (έως 3 μετακινήσεις)", "0,90€", "0,45€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("Εισιτήριο 10+1 ελεύθερο", "6,00€", "3,00€")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    TableRow("Διάρκειας 30 ημερών (Κάρτα)", "30,00€", "15,00€")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            InfoNoteCard(
                text = "Τα χρονικά εισιτήρια επιτρέπουν την επιβίβαση σε διαφορετικά λεωφορεία ή και στο Μετρό εντός του χρονικού ορίου από την πρώτη επικύρωση."
            )
        }
    }
}

fun LazyColumn.discountsTabSection() {
    item {
        Text("Εδώ θα μπουν οι εκπτώσεις", modifier = Modifier.padding(16.dp))
    }
}

fun LazyColumn.locationsTabSection() {
    item {
        Text("Εδώ θα μπουν τα σημεία πώλησης", modifier = Modifier.padding(16.dp))
    }
}

fun LazyColumn.penaltiesTabSection() {
    item {
        Text("Εδώ θα μπουν τα πρόστιμα", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun TableRow(title: String, normalPrice: String, reducedPrice:String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text( title, modifier = Modifier.weight(1.8f), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface )
        Text( normalPrice, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Text( reducedPrice, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    }
}
