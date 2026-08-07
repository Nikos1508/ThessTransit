package com.example.thesstransit.ui.item

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesstransit.ui.data.TutorialPages
import com.example.thesstransit.ui.data.TutorialState
import com.example.thesstransit.ui.data.CardPosition

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TutorialOverlay(
    tutorialState: TutorialState,
    listState: LazyListState,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {

    val page = tutorialState.currentPage
    val targetInfo = tutorialState.targets[page.target]
    val targetRect = targetInfo?.rect
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

        val scrollOffset =
            if (listState.firstVisibleItemIndex == 0)
                listState.firstVisibleItemScrollOffset.toFloat()
            else
                0f

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
                left = animatedRect.left - scrollOffset - padding,
                top = animatedRect.top - padding,
                right = animatedRect.right + padding,
                bottom = animatedRect.bottom - scrollOffset + padding,
            )

            drawRoundRect(
                color = Color.Transparent,
                topLeft = spotlight.topLeft,
                size = spotlight.size,
                cornerRadius = CornerRadius(
                    28.dp.toPx(),
                    28.dp.toPx()
                ),
                blendMode = BlendMode.Clear
            )

            drawRoundRect(
                color = glowColor,
                topLeft = spotlight.topLeft,
                size = spotlight.size,
                cornerRadius = CornerRadius(
                    28.dp.toPx(),
                    28.dp.toPx()
                ),
                style = Stroke( width = 2.dp.toPx() )
            )
        }

//        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
//
//        val threshold = with(LocalDensity.current) {
//            screenHeight.toPx() * 0.5f
//        }
//
//        val centerY = targetRect?.center?.y ?: 0f
//
//        val cardAlignment =
//            if (centerY < threshold)
//                Alignment.BottomCenter
//            else
//                Alignment.TopCenter

        val cardAlignment =
            if (page.cardPosition == CardPosition.BOTTOM)
                Alignment.BottomCenter
            else
                Alignment.TopCenter

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = cardAlignment
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(400)
                ) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(400)
                )+ slideInVertically (
                    animationSpec = tween(400),
                    initialOffsetY = {
                        it / 3
                    }
                )
            ) {
                TutorialCard(
                    modifier = Modifier
                        .align(cardAlignment)
                        .padding(horizontal = 20.dp)
                        .padding(
                            top =
                                if (page.cardPosition == CardPosition.TOP)
                                    32.dp
                                else
                                    0.dp,
                            bottom =
                                if (page.cardPosition == CardPosition.BOTTOM)
                                    32.dp
                                else
                                    0.dp
                        ),
                    step = tutorialState.currentStep,
                    onNext = onNext,
                    onSkip = onSkip
                )
            }
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

    val page = TutorialPages[step]

    Surface(
        shadowElevation = 18.dp,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = TutorialPages[step].icon,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(12.dp))

                Column {
                    Text(
                        text = stringResource(page.titleRes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )

                    Text(
                        text = "${step + 1} / ${TutorialPages.size}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TutorialProgress(
                current = step,
                total = TutorialPages.size
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(page.descriptionRes),
                lineHeight = 22.sp,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row {

                if (step != TutorialPages.lastIndex) {
                    TextButton(
                        onClick = onSkip
                    ) {
                        Text("Skip")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

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
    }
}

@Composable
fun TutorialProgress(
    current: Int,
    total: Int
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(total) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            if (index <= current)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer( modifier = Modifier.height(10.dp) )

        Text(
            text = "${current + 1} / $total",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}