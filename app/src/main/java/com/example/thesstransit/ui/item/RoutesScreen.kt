package com.example.thesstransit.ui.item

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.Oasth
import xyz.teogramm.oasth.OasthData
import xyz.teogramm.oasth.base.BusMasterLine

@Composable
fun RoutesScreen() {
    var oasthData by remember { mutableStateOf<OasthData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val fetchedData = Oasth().fetchData()

                withContext(Dispatchers.Main) {
                    oasthData = fetchedData
                    isLoading = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Text(text = "Φόρτωση γραμμών...")
    } else {

        val allMasterLines: Map<Int, BusMasterLine> = oasthData?.masterLines ?: emptyMap()

        println(allMasterLines)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(allMasterLines.values.toList()) { masterLine ->
                Text(text = "${masterLine.number}: ${masterLine.nameEL}")
            }
        }

    }
}