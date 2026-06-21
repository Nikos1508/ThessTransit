package com.example.thesstransit.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitlab.mitsiosm.oseth.data.Route

@Composable
fun BusRouteRowItem(route: Route) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ){

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = route.shortName,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column( modifier = Modifier.weight(1f) ) {

                val shouldScroll = route.longName.length > 25

                Text(
                    text = route.longName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = if (shouldScroll) {
                        Modifier.fillMaxWidth().basicMarquee(Int.MAX_VALUE)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                    softWrap = false
                )

                Spacer( modifier = Modifier.height(4.dp) )

                Text(
                    text = "Γραμμή: ${route.shortName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val safeColor = try {
                val raw = route.color.trim()
                val normalized = if (raw.startsWith("#")) raw else "#$raw"
                Color(android.graphics.Color.parseColor(normalized))
            } catch (e: Exception) {
                Color.Gray
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = safeColor,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}