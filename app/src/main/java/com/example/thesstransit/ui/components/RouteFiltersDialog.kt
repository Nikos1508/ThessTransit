package com.example.thesstransit.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

data class RouteFilterResults(
    val fastRoute: Boolean,
    val lessWalking: Boolean,
    val avoidMetro: Boolean,
    val avoidTransfer: Boolean,
    val timeType: String,
    val hour: Int,
    val minute: Int
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFiltersDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (RouteFilterResults) -> Unit,
    initialHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    initialMinute: Int = Calendar.getInstance().get(Calendar.MINUTE)
){
    //Κατάσταση φίλτρων
    var fastRoute by remember { mutableStateOf(true) }
    var lessWalking by remember { mutableStateOf(false)}
    var avoidMetro by remember { mutableStateOf(false) }
    var avoidTransfer by remember { mutableStateOf(false) }

    // "DEPART" ή "ARRIVE"
    var timeType by remember { mutableStateOf("DEPART") }

    // Καταστάσεις Ώρας
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Αναχώρηση", "Άφιξη")
    val selectedTabIndex = if (timeType == "DEPART") 0 else 1

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Προτιμήσεις Διαδρομής",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Προγραμματισμός Ώρας",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) },
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                            )
                        }
                    }
                ){
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index

                        Tab(
                            selected = isSelected,
                            onClick = { timeType = if (index == 0) "DEPART" else "ARRIVE"},
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                RouteTimeSelector(
                    hour = selectedHour,
                    minute = selectedMinute,
                    timeType = timeType
                ){
                    showTimePickerDialog = true
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Κριτήρια",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    RouteFilterSwitch(title = "Ταχύτερη διαδρομή", checked = fastRoute) { fastRoute = it }
                    RouteFilterSwitch(title = "Λιγότερο Περπάτημα", checked = lessWalking) { lessWalking = it }
                    RouteFilterSwitch(title = "Αποφυγή Μετρό", checked = avoidMetro) { avoidMetro = it }
                    RouteFilterSwitch(title = "Ελάχιστες Μετεπιβάσεις", checked = avoidTransfer) { avoidTransfer = it }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        onApplyFilters(
                            RouteFilterResults(
                                fastRoute, lessWalking, avoidMetro, avoidTransfer, timeType, selectedHour, selectedMinute
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Εφαρμογή Φίλτρων", fontWeight = FontWeight.Bold)
                }
            }
        }
    )

    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = true
    )

    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePickerDialog = false
                }) {
                    Text("Εντάξει", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("Ακύρωση")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (timeType == "DEPART") "Επιλογή ώρας αναχώρησης" else "Επιλογή ώρας άφιξης",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun RouteTimeSelector(
    hour: Int,
    minute: Int,
    timeType: String,
    onClick: () -> Unit
){
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = if (timeType == "DEPART") "Ώρα Αναχώρησης" else "Ώρα Άφιξης",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format("%02d:%02d", hour, minute),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RouteFilterSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackColor by animateColorAsState(
        if (checked)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(50))
                .background(trackColor)
                .clickable {
                    onCheckedChange(!checked)
                }
                .padding(3.dp)
        ){
            Box(
                modifier = Modifier
                    .align(
                        if (checked)
                            Alignment.CenterEnd
                        else
                            Alignment.CenterStart
                    )
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RouteFiltersDialogPreview() {
    MaterialTheme {
        RouteFiltersDialog(
            onDismiss = {},
            onApplyFilters = {},
            initialHour = 12,
            initialMinute = 0f.toInt()
        )
    }
}