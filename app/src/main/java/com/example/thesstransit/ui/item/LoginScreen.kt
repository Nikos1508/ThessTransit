package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.thesstransit.R
import com.example.thesstransit.ui.components.AnimatedBackground
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .systemBarsPadding()
    ) {
        AnimatedBackground()

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            LoginContent(
                onLoginClick = onLoginClick
            )

        }
    }
}

@Composable
fun  LoginContent(
    onLoginClick: () -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val glowTransition = rememberInfiniteTransition(label = "glow")

    val glowScale by glowTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var step by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        repeat(6) {
            delay(100.milliseconds)
            step++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(34.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            )
            .background(
                Brush.radialGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
                        Color.Transparent
                    )
                ),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .widthIn(max = 420.dp)
                .border(
                    BorderStroke(
                        width = 1.dp,
                        Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(size = 34.dp)
                ),
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 26.dp,
                        vertical = 34.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AnimatedVisibility(
                    visible = step >= 1,
                    enter = fadeIn() + scaleIn()
                ) {
                    FloatingLogo()
                }

                Spacer(Modifier.height(26.dp))

                AnimatedVisibility(
                    visible = step >= 2,
                    enter = fadeIn()
                ) {
                    Text(
                        text = stringResource(R.string.btn_login),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = step >= 2,
                    enter = fadeIn()
                ) {
                    Text(
                        stringResource(R.string.login_subtitle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer( modifier = Modifier.height(8.dp) )

                AnimatedVisibility(
                    visible = step >= 3,
                    enter = fadeIn() + scaleIn()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LoginEmailField(
                            value = email,
                            onValueChange = { email = it }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = step >= 4,
                    enter = fadeIn() + scaleIn()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LoginPasswordField(
                            value = password,
                            onValueChange = { password = it },
                            visible = passwordVisible,
                            onVisibilityChange = {
                                passwordVisible = !passwordVisible
                            }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn()
                ) {
                    PremiumLoginButton(
                        onClick = onLoginClick
                    )
                }

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn()
                ) {
                    ForgotPasswordButton()
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = step >= 6,
                    enter = fadeIn()
                ) {
                    LoginDivider()
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = step >= 6,
                    enter = fadeIn()
                ) {
                    RegisterRow()
                }
            }

        }
    }
}

@Composable
fun FloatingLogo() {
    val transition = rememberInfiniteTransition(label = "logo")

    val offsetY by transition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val iconColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY
            }
            .size(88.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(0.30f),
                        MaterialTheme.colorScheme.primary.copy(0.10f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawCircle(
                color = circleColor,
                radius = size.minDimension / 2.3f
            )
        }

        Icon(
            imageVector = Icons.Outlined.DirectionsBus,
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            tint = iconColor
        )
    }
}

@Composable
fun LoginEmailField(
    value: String,
    onValueChange: (String) -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .onFocusChanged {
                focused = it.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = if (focused)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                stringResource(R.string.email_placeholder),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun LoginPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .onFocusChanged {
                focused = it.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(size = 20.dp),
        visualTransformation = if (visible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = if (focused)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onVisibilityChange
            ) {
                Icon(
                    imageVector = if (visible)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                    contentDescription = null
                )
            }
        },
        placeholder = { Text( stringResource(R.string.password_placeholder) ) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun RegisterRow() {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text( stringResource(R.string.no_account_prompt) )

        Text(
            stringResource(R.string.link_register),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures {

                }
            }
        )
    }
}

@Composable
fun LoginDivider() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )

        Text(
            stringResource(R.string.divider_or),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    }
}

@Composable
fun ForgotPasswordButton() {

    TextButton(
        onClick = {}
    ) {
        Text(
            stringResource(R.string.btn_forgot_password),
            color = MaterialTheme.colorScheme.primary
        )
    }

}

@Composable
fun PremiumLoginButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "scale"
    )

    val transition = rememberInfiniteTransition(label = "button")

    val arrowOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            RepeatMode.Reverse
        ),
        label = "arrow"
    )

    val shine by transition.animateFloat(
        initialValue = -350f,
        targetValue = 650f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2800,
                delayMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(0.82f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            }
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            rotate(-22f) {
                drawRect(
                    brush = Brush.linearGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),

                    topLeft = Offset(
                        shine,
                        -200f
                    ),

                    size = Size(
                        70f,
                        size.height + 400f
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Σύνδεση",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                null,
                tint = Color.White,
                modifier = Modifier.graphicsLayer{
                    translationX = arrowOffset
                }
            )
        }
    }
}