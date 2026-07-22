package com.example.thesstransit.ui.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
        mutableStateOf(0)
    }

    val cardTransition = rememberInfiniteTransition()

    val offset by cardTransition.animateFloat(

        initialValue = -3f,
        targetValue = 3f,

        animationSpec = infiniteRepeatable(
            tween(
                5000,
                easing = FastOutSlowInEasing
            ),
            RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        repeat(6) {
            delay(100.milliseconds)
            step++
        }
    }

    Box(
        modifier = Modifier
            .size(420.dp)
            .graphicsLayer{
                scaleX = glowScale
                scaleY = glowScale
            }
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
                .graphicsLayer{
                    translationY = offset
                }
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
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
                        "Καλοσήρθατε πίσω",
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
                        "Συνδεθείτε για διαθέσιμα δεδομενα σε πολλαπλες συσκευές",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = step >= 3,
                    enter = fadeIn()
                ) {
                    LoginEmailField(
                        value = email,
                        onValueChange = {email = it}
                    )
                }

                Spacer(Modifier.height(18.dp))

                AnimatedVisibility(
                    visible = step >= 4,
                    enter = fadeIn()
                ) {
                    LoginPasswordField(
                        value = password,
                        onValueChange = {password = it},
                        visible = passwordVisible,
                        onVisibilityChange = {
                            passwordVisible = !passwordVisible
                        }
                    )
                }

                Spacer(Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn()
                ) {
                    LoginButton(
                        onClick = onLoginClick
                    )
                }

                Spacer(Modifier.height(22.dp))

                AnimatedVisibility(
                    visible = step >= 6,
                    enter = fadeIn()
                ) {
                    TextButton( onClick = {} ) {
                        Text("Συνέχεια ως επισκέπτης")
                    }
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
        Icon(
            imageVector = Icons.Outlined.DirectionsBus,
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            tint = MaterialTheme.colorScheme.primary

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
            .onFocusChanged {
                focused = it.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint =
                    if (focused)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = { Text("Email") },
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
            .onFocusChanged{
                focused = it.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(size = 20.dp),
        visualTransformation =
            if (visible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                Icons.Outlined.Lock,
                null,
                tint =
                    if(focused)
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
                    if (visible)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                    null
                )
            }
        },
        placeholder = { Text("Password") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f),
            focusedIndicatorColor = Color.Transparent
        )
    )
}