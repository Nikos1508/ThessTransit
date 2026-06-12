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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.thesstransit.ui.theme.ThessTransitTheme


data class BottomBarItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun ThessTransitBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
){

    val items = listOf(
        BottomBarItem(title = "Εισιτήρια", icon = Icons.Outlined.LocalActivity),
        BottomBarItem(title = "Αρχική", icon = Icons.Outlined.Home),
        BottomBarItem(title = "Login", icon = Icons.Outlined.Person)
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    BottomBarNavItem(
                        item = item,
                        selected = index == selectedIndex,
                        onClick = {
                            onItemSelected(index)
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(
                        WindowInsets.navigationBars
                    )
                )

            }
        }
    }
}

@Composable
private fun BottomBarNavItem(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundShape = RoundedCornerShape(26.dp)

    val iconTint by animateColorAsState(
        targetValue =
            if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onBackground,
        label = ""
    )

    val textTint by animateColorAsState(
        targetValue =
            if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onBackground,
        label = ""
    )

    val width by animateDpAsState(
        targetValue =
            if (selected)
                130.dp
            else
                88.dp,
        animationSpec = spring(),
        label = ""
    )

    Box(
        modifier = Modifier
            .height(54.dp)
            .then(
                if (selected) {
                    Modifier
                        .clip(backgroundShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        )
                } else{
                    Modifier
                }
            )
            .clickable{onClick()}
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier.width(width),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = iconTint,
                modifier = Modifier.size( if(selected) 24.dp else 22.dp )
            )

            if (selected) {
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = item.title,
                    color = textTint,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
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