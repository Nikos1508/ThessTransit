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
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesstransit.R
import com.example.thesstransit.ui.viewModels.LoginViewModel
import com.example.thesstransit.ui.viewModels.RegisterViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RegisterScreen(
    viewModel:RegisterViewModel = viewModel(),
    onRegisterSuccess:()->Unit,
    onBackClick:()->Unit
){
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
        RegisterContent(
            viewModel = viewModel,
            onSuccess = onRegisterSuccess,
            onBackClick = onBackClick
        )
    }
}


@Composable
fun RegisterContent(
    viewModel: RegisterViewModel,
    onSuccess:()->Unit,
    onBackClick:()->Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegistered){
        if(uiState.isRegistered){
            onSuccess()
        }
    }

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
                        text = "Δημιουργία λογαριασμού",
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
                        text = "Δημιούργησε τον λογαριασμό σου στο ThessTransit",
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
                            value = uiState.email,
                            error = uiState.emailError,
                            loading = uiState.isLoading,
                            onValueChange = viewModel::onEmailChange
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
                            value = uiState.password,
                            error = uiState.passwordError,
                            loading = uiState.isLoading,
                            onValueChange = viewModel::onPasswordChange,
                            visible = uiState.passwordVisible,
                            onVisibilityChange = viewModel::togglePasswordVisibility,
                            onDone = { viewModel.register() }
                        )
                    }
                }

                Spacer(
                    Modifier.height(20.dp)
                )

                RegisterPasswordField(
                    value = uiState.confirmPassword,
                    error = uiState.confirmPasswordError,
                    loading = uiState.isLoading,
                    visible = uiState.confirmPasswordVisible,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    onVisibilityChange = viewModel::toggleConfirmPasswordVisibility,
                    onDone = { viewModel.register() }
                )

                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = slideInVertically {
                        -it / 2
                    } + fadeIn()
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn()
                ) {
                    PremiumLoginButton(
                        text="Εγγραφή",
                        enabled=!uiState.isLoading,
                        loading=uiState.isLoading,
                        onClick={ viewModel.register() }
                    )
                }

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = step >= 5,
                    enter = fadeIn()
                ) {
                    TextButton(
                        enabled=!uiState.isLoading,
                        onClick=onBackClick
                    ){
                        Text(
                            "Έχεις ήδη λογαριασμό; Σύνδεση"
                        )
                    }
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
                    LoginRow(
                        enabled=!uiState.isLoading,
                        onClick = onBackClick
                    )
                }
            }

        }
    }
}

@Composable
fun RegisterPasswordField(
    value: String,
    error: String?,
    loading: Boolean,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityChange: () -> Unit,
    onDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        isError = error != null,
        visualTransformation =
            if (visible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone()
            }
        ),
        leadingIcon = {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null
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
        label = { Text("Επιβεβαίωση κωδικού") },
        supportingText = {
            if (error != null) {
                Text(error)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun PremiumLoginButton(
    text: String,
    onClick:() -> Unit,
    enabled:Boolean = true,
    loading:Boolean = false
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

    val haptic = LocalHapticFeedback.current
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
            .alpha(
                if (enabled) 1f else 0.5f
            )
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
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            tryAwaitRelease()
                            pressed = false
                            haptic.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                            onClick()
                        }
                    )
                }
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

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!loading) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    null,
                    tint = Color.White,
                    modifier = Modifier.graphicsLayer {
                        translationX = arrowOffset
                    }
                )
            }
        }
    }
}

@Composable
fun LoginRow(
    enabled:Boolean,
    onClick:()->Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            "Έχεις ήδη λογαριασμό;"
        )

        Text(
            "Σύνδεση",
            color=MaterialTheme.colorScheme.primary,
            fontWeight=FontWeight.Bold,
            modifier=Modifier.clickable{ onClick() }
        )
    }
}