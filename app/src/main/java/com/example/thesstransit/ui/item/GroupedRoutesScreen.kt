package com.example.thesstransit.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thesstransit.ui.data.RouteGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun GroupedRoutesScreen(
    groups: List<RouteGroup>,
    favoriteGroups: Set<String>,
    onGroupClick: (RouteGroup) -> Unit,
    onFavoriteClick: (String) -> Unit
){
    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(groups) { group ->
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{ onGroupClick(group) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.groupId,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${group.routes.size} γραμμές",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(12.dp))

                    IconButton(
                        onClick = { onFavoriteClick(group.groupId) }
                    ) {
                        Icon(
                            imageVector =
                                if (favoriteGroups.contains(group.groupId))
                                    Icons.Default.Favorite
                                else
                                    Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (favoriteGroups.contains(group.groupId))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}