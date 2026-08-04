package com.example.thesstransit.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.thesstransit.ui.data.TutorialState
import com.example.thesstransit.ui.data.TutorialTarget
import com.example.thesstransit.ui.data.tutorialTarget

@Composable
fun TutorialAnchor(
    modifier: Modifier = Modifier,
    target: TutorialTarget,
    tutorialState: TutorialState,
    itemIndex: Int,
    content: @Composable () -> Unit
) {

    Box(
        modifier = modifier.tutorialTarget(
            target = target,
            tutorialState = tutorialState,
            itemIndex = itemIndex
        )
    ) {
        content()
    }
}