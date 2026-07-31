package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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

    val page = TutorialPages[tutorialState.currentStep]
    val targetRect = tutorialState.targets[page.target]
    val transition = rememberInfiniteTransition(
        label = "TutorialFlow"
    )

    val glowAlpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Glow"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val animatedRect by animateRectAsState(
            targetValue = targetRect ?: Rect.Zero,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            label = "TutorialRect"
        )

        val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy =
                        CompositingStrategy.Offscreen
                }
        ) {
            drawRect(
                Color.Black.copy(alpha = 0.32f)
            )

            val padding = 10.dp.toPx()
            val spotlight = Rect(
                left = animatedRect.left - padding,
                top = animatedRect.top - padding,
                right = animatedRect.right + padding,
                bottom = animatedRect.bottom + padding,
            )

            drawRoundRect(
                color = Color.Transparent,
                topLeft = spotlight.topLeft,
                size = spotlight.size,
                cornerRadius = CornerRadius(
                    22.dp.toPx(),
                    22.dp.toPx()
                ),
                blendMode = BlendMode.Clear
            )

            drawRoundRect(
                color = glowColor,
                topLeft = spotlight.topLeft,
                size = spotlight.size,
                cornerRadius = CornerRadius(
                    22.dp.toPx(),
                    22.dp.toPx()
                ),
                style = Stroke( width = 2.dp.toPx() )
            )
        }

        val cardAlignment =
            if ( (targetRect?.top ?: 0f) < 500f ) {
                Alignment.BottomCenter
            } else {
                Alignment.TopCenter
            }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = tween(500)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(500)
            )+ slideInVertically {
                it / 3
            }
        ) {
            TutorialCard(
                modifier = Modifier
                    .align(cardAlignment)
                    .padding(20.dp),
                step = tutorialState.currentStep,
                onNext = onNext,
                onSkip = onSkip
            )
        }

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
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(TutorialPages.size) { index ->

                    Box(
                        modifier = Modifier
                            .size(width = if (index == step) 18.dp else 6.dp, height = 6.dp)
                            .clip( RoundedCornerShape(20.dp))
                            .background(
                                if (index == step)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }

            // Spacer( modifier = Modifier.height(10.dp) )

            Text(
                TutorialPages[step].description
            )

            Spacer( modifier = Modifier.height(24.dp) )

            Row{
                TextButton(
                    onClick = onSkip
                ) {
                    Text(
                        "Skip"
                    )
                }

                Spacer( modifier = Modifier.weight(1f) )

                FilledTonalButton(
                    onClick = onNext
                ) {
                    Text(
                        if (step == TutorialPages.lastIndex)
                            "Finish"
                        else
                            "Next"
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = TutorialPages[step].icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer( modifier = Modifier.width(12.dp) )

            Text(
                TutorialPages[step].title,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp
            )
        }
    }
}