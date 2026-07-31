package com.example.thesstransit.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesstransit.ui.data.TutorialPages
import com.example.thesstransit.ui.data.TutorialState

@Composable
fun TutorialOverlay(
    tutorialState: TutorialState,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.30f))
    ) {
        TutorialCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            step = tutorialState.currentStep,
            onNext = onNext,
            onSkip = onSkip
        )
    }
}

@Composable
fun TutorialCard(
    modifier: Modifier = Modifier,
    step: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                TutorialPages[step].title,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp
            )

            Spacer( modifier = Modifier)
        }
    }
}