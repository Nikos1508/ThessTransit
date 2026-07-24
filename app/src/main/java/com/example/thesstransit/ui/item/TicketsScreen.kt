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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.ScreenHeader


@Composable
fun TicketsScreen(
    onBackClick: () -> Unit
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Τιμές & Είδη", "Εκπτώσεις", "Πού αγοράζω εισιτήρια", "Πρόστιμα")
    Column {
        ScreenHeader(
            title = "Εισητήρια",
            onBackClick = onBackClick,
            onProfileClick = onBackClick
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
                        text = stringResource(R.string.tickets),
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

                PrimaryScrollableTabRow(
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

                    if (selectedTab == 0) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Εισητήρια λεωφορείου",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.4f
                                                    )
                                                )
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                "Τύπος Εισητήριου",
                                                modifier = Modifier.weight(1.8f)
                                            )
                                            Text(
                                                "Κανονικό",
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "Μειωμένο",
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End,
                                                fontSize = 14.sp
                                            )
                                        }

                                        TableRow("Αστικής Ζώνης", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("Περιαστικής Ζώνης", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("Αεροδρόμιο (1Χ)", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("10+1 Αστικής Ζώνης", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("10+1 Περιαστικής Ζώνης", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("30 Ημερών", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("60 Ημερών", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        TableRow("90 Ημερών", "0,60€", "0,30€")
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                InfoNoteCard(
                                    text = "Τα χρονικά εισιτήρια επιτρέπουν την επιβίβαση σε διαφορετικά λεωφορεία ή και στο Μετρό εντός του χρονικού ορίου από την πρώτη επικύρωση."
                                )
                            }
                        }
                    }

                    if (selectedTab == 1) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Δικαιούχοι Μειωμένου Κομίστρου",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.Percent,
                                    title = "Ποιοι πληρώνουν Μειωμένο (50%)",
                                    description = "• Φοιτητές ελληνικών δημόσιων πανεπιστημίων (με επίδειξη ακαδημαϊκής ταυτότητας)\n" +
                                            "• Μαθητές σχολείων Πρωτοβάθμιας & Δευτεροβάθμιας εκπαίδευσης\n" +
                                            "• Νέοι ηλικίας 7 έως 18 ετών\n" +
                                            "• Ηλικιωμένοι άνω των 65 ετών (με επίδειξη αστυνομικής ταυτότητας)"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoCard(
                                    icon = Icons.Outlined.ConfirmationNumber,
                                    title = "Ποιοι μετακινούνται Δωρεάν",
                                    description = "• Παιδιά έως 6 ετών (πρέπει να συνοδεύονται από ενήλικα)\n" // +
                                    // "• Άνεργοι εγγεγραμμένοι στη ΔΥΠΑ (με πρόσφατη ψηφιακή βεβαίωση)\n" +
                                    // "• Άτομα με Αναπηρία (ΑμεΑ) με την επίδειξη της ειδικής κάρτας"
                                )
                            }
                        }
                    }

                    if (selectedTab == 2) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Πού θα εκδώσεις εισιτήριο ή κάρτα",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.CreditCard,
                                    title = "Προσωποποιημένη Κάρτα (ThessCard)",
                                    description = "Μπλα Μπλε Μπλι Μπλο" /* TODO */
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoCard(
                                    icon = Icons.Outlined.Storefront,
                                    title = "Φυσικά σημεία & Εκδοτήρια",
                                    description = "Μπλα Μπλε Μπλι Μπλο" /* TODO */
                                )
                            }
                        }
                    }

                    if (selectedTab == 3) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Έλεγχοι Κομίστρου & Πρόστημα",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.Gavel,
                                    title = "Μη επικύρωση εισιτηρίου",
                                    description = "Μπλα Μπλε Μπλι Μπλο"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoCard(
                                    icon = Icons.Outlined.Info,
                                    title = "Έκπτωση 50% στην εξόφλιση",
                                    description = "Μπλα Μπλε Μπλι Μπλο"
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
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

@Composable
fun InfoCard(icon: ImageVector, title: String, description: String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ){
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 4.dp))

            Spacer(modifier = Modifier.width(14.dp))

            Column{
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoNoteCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}