package com.example.thesstransit.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thesstransit.ui.theme.ThessTransitTheme


data class BottomBarItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun ThessTransitBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {

    val items = listOf(
        BottomBarItem(
            "Εισιτήρια",
            Icons.Outlined.LocalActivity
        ),
        BottomBarItem(
            "Αρχική",
            Icons.Outlined.Home
        ),
        BottomBarItem(
            "Δρομολόγια",
            Icons.Outlined.Route
        )
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->

                BottomBarNavItem(
                    item = item,
                    selected = selectedIndex == index,
                    onClick = {
                        onItemSelected(index)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBarNavItem(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    val iconTint by animateColorAsState(
        targetValue =
            if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )

    val textTint by animateColorAsState(
        targetValue =
            if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )

    val pillWidth by animateDpAsState(
        targetValue =
            if (selected)
                96.dp
            else
                48.dp,
        animationSpec = spring(),
        label = ""
    )

    val pillHeight by animateDpAsState(
        targetValue =
            if (selected)
                46.dp
            else
                0.dp,
        animationSpec = spring(),
        label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClick()
        }
    ) {

        Box(
            modifier = Modifier
                .width(pillWidth)
                .height(
                    if (selected)
                        pillHeight
                    else
                        46.dp
                ),
            contentAlignment = Alignment.Center
        ) {

            if (selected) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }

            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = item.title,
            color = textTint,
            style = MaterialTheme.typography.labelMedium,
            fontWeight =
                if (selected)
                    FontWeight.SemiBold
                else
                    FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarHomeSelectedLightPreview() {

    ThessTransitTheme(darkTheme = false) {
        ThessTransitBottomBar(
            selectedIndex = 1,
            onItemSelected = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarHomeSelectedDarkPreview() {

    ThessTransitTheme(darkTheme = true) {
        ThessTransitBottomBar(
            selectedIndex = 1,
            onItemSelected = {}
        )
    }

}


@Preview(showBackground = true)
@Composable
fun BottomBarTicketsSelectedLightPreview() {

    ThessTransitTheme(darkTheme = false) {
        ThessTransitBottomBar(
            selectedIndex = 0,
            onItemSelected = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarTicketsSelectedDarkPreview() {

    ThessTransitTheme(darkTheme = true) {
        ThessTransitBottomBar(
            selectedIndex = 0,
            onItemSelected = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarProfileSelectedLightPreview() {

    ThessTransitTheme(darkTheme = false) {
        ThessTransitBottomBar(
            selectedIndex = 2,
            onItemSelected = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarProfileSelectedDarkPreview() {

    ThessTransitTheme(darkTheme = true) {
        ThessTransitBottomBar(
            selectedIndex = 2,
            onItemSelected = {}
        )
    }

}