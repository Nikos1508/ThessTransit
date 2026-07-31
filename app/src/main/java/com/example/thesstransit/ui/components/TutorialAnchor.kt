package com.example.thesstransit.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.thesstransit.ui.data.TutorialState
import com.example.thesstransit.ui.data.TutorialTarget
import com.example.thesstransit.ui.data.tutorialTarget

@Composable
fun TutorialAnchor(
    target: TutorialTarget,
    tutorialState: TutorialState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .tutorialTarget(
                target,
                tutorialState
            )
    ) {
        content()
    }

}