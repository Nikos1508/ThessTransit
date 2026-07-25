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

    val tabs = listOf(
        stringResource(R.string.tab_prices_and_types),
        stringResource(R.string.tab_discounts),
        stringResource(R.string.tab_sales_points),
        stringResource(R.string.tab_fines)
    )

    Column {
        ScreenHeader(
            title = stringResource(R.string.tickets_screen_title),
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
                        text = stringResource(R.string.tickets_header_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.tickets_header_subtitle),
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
                                    text = stringResource(R.string.section_bus_tickets),
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
                                                text = stringResource(R.string.table_header_ticket_type),
                                                modifier = Modifier.weight(1.8f)
                                            )
                                            Text(
                                                text = stringResource(R.string.table_header_regular),
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = stringResource(R.string.table_header_reduced),
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End,
                                                fontSize = 14.sp
                                            )
                                        }

                                        TableRow(
                                            title = stringResource(R.string.ticket_urban_zone),
                                            normalPrice = stringResource(R.string.price_0_60),
                                            reducedPrice = stringResource(R.string.price_0_30)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_suburban_zone),
                                            normalPrice = stringResource(R.string.price_0_80),
                                            reducedPrice = stringResource(R.string.price_0_40)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_airport),
                                            normalPrice = stringResource(R.string.price_2_00),
                                            reducedPrice = stringResource(R.string.price_1_00)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_daily),
                                            normalPrice = stringResource(R.string.price_2_50),
                                            reducedPrice = stringResource(R.string.price_2_50)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_10_plus_1_urban),
                                            normalPrice = stringResource(R.string.price_5_80),
                                            reducedPrice = stringResource(R.string.price_2_90))
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_10_plus_1_suburban),
                                            normalPrice = stringResource(R.string.price_7_80),
                                            reducedPrice = stringResource(R.string.price_3_90)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_30_days),
                                            normalPrice = stringResource(R.string.price_16_00),
                                            reducedPrice = stringResource(R.string.price_8_00)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_90_days),
                                            normalPrice = stringResource(R.string.price_45_00),
                                            reducedPrice = stringResource(R.string.price_22_50)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                        TableRow(
                                            title = stringResource(R.string.ticket_180_days),
                                            normalPrice = stringResource(R.string.price_85_00),
                                            reducedPrice = stringResource(R.string.price_42_50)
                                        )
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                InfoNoteCard(
                                    text = stringResource(R.string.tickets_info_note)
                                )
                            }
                        }
                    }

                    if (selectedTab == 1) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = stringResource(R.string.section_reduced_fare_beneficiaries),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.Percent,
                                    title = stringResource(R.string.title_who_pays_reduced),
                                    description = stringResource(R.string.title_who_pays_reduced)
                                )

                                Spacer( modifier = Modifier.height(16.dp) )

                                InfoCard(
                                    icon = Icons.Outlined.ConfirmationNumber,
                                    title = stringResource(R.string.title_who_travels_free),
                                    description = stringResource(R.string.desc_who_travels_free)
                                )
                            }
                        }
                    }

                    if (selectedTab == 2) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = stringResource(R.string.section_where_to_buy),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.CreditCard,
                                    title = stringResource(R.string.title_thesscard),
                                    description = stringResource(R.string.title_thesscard),
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoCard(
                                    icon = Icons.Outlined.Storefront,
                                    title = stringResource(R.string.title_physical_points),
                                    description = stringResource(R.string.desc_physical_points)
                                )
                            }
                        }
                    }

                    if (selectedTab == 3) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = stringResource(R.string.section_fines_title),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                InfoCard(
                                    icon = Icons.Outlined.Gavel,
                                    title = stringResource(R.string.title_no_ticket_fine),
                                    description = stringResource(R.string.desc_no_ticket_fine)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                InfoCard(
                                    icon = Icons.Outlined.Info,
                                    title = stringResource(R.string.title_fine_discount),
                                    description = stringResource(R.string.desc_fine_discount)
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