package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.ui.components.BusRouteRowItem
import com.example.thesstransit.ui.components.ScreenHeader
import com.example.thesstransit.ui.viewModels.RoutesViewModel
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun RoutesScreen(
    onBackClick: () -> Unit,
    viewModel: RoutesViewModel = viewModel()
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    val routes = viewModel.routes
    val loading = viewModel.isLoading
    val error = viewModel.errorMessage

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

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(text = "Αναζήτηση γραμμής...", fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                )

                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),

                        )
                    }

                    else -> {
                        val filteredRoutes = routes.filter {

                            it.shortName.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                            ||

                            it.longName.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                        }

                        val groupedRoutes =
                            filteredRoutes.groupBy {
                            it.shortName.firstOrNull()?.toString() ?: "#"
                            }

                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val sectionIndexes = mutableMapOf<String, Int>()

                            var currentIndex = 0

                            groupedRoutes.forEach { (digit, routesInSection) ->

                                sectionIndexes[digit] = currentIndex

                                currentIndex++

                                currentIndex += routesInSection.size
                            }

                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = 16.dp,
                                        end = 56.dp
                                    ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                groupedRoutes.forEach { (digit, routes) ->

                                    stickyHeader {

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.background)
                                                .padding(vertical = 8.dp)
                                        ){
                                            Text(
                                                text = digit,
                                                fontSize = 22.sp,
                                                modifier = Modifier.padding(start = 4.dp),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    items(routes) { route ->
                                        BusRouteRowItem(route)
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 6.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                groupedRoutes.keys.sorted().forEach {digit ->

                                    Text(
                                        text = digit,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable {
                                                scope.launch {
                                                    listState.animateScrollToItem(
                                                        sectionIndexes[digit] ?: 0
                                                    )
                                                }
                                            }
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}