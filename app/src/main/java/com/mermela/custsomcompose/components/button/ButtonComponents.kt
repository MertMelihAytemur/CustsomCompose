package com.mermela.custsomcompose.components.button

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle

sealed class ButtonState {
    object Normal : ButtonState()
    object Loading : ButtonState()
    object Done : ButtonState()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StateButton() {
    var touched by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (touched) .94f else 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    var buttonState: ButtonState by remember { mutableStateOf(ButtonState.Normal) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (buttonState == ButtonState.Normal) {
                            touched = true
                        }
                        awaitRelease()
                        touched = false
                    }
                ) {
                    if (buttonState == ButtonState.Normal) {
                        scope.launch {
                            buttonState = ButtonState.Loading
                            delay(2000)
                            buttonState = ButtonState.Done
                            delay(1500)
                            buttonState = ButtonState.Normal
                        }
                    }
                }
            }
            .scale(scale)
            .background(
                Color.Black,
                CircleShape
            )
            .clip(CircleShape)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.White,
            LocalTextStyle provides androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Medium)
        ) {
            val springSpec: FiniteAnimationSpec<IntOffset> = remember {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            }
            AnimatedContent(
                targetState = buttonState,
                transitionSpec = {
                    slideInVertically(
                        springSpec
                    ) { -it } + fadeIn() with slideOutVertically(
                        springSpec
                    ) { it } + fadeOut() using SizeTransform(clip = false)
                },
                contentAlignment = Alignment.Center,
            ) { buttonState ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (buttonState) {
                        ButtonState.Normal -> {
                            Text(text = "Start")
                        }

                        ButtonState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFF3CC4E),
                                strokeWidth = 3.dp
                            )
                        }

                        ButtonState.Done -> {
                            Text(text = "Done", color = Color(0xFF60E76D))
                            Image(
                                modifier = Modifier.padding(start = 5.dp).size(16.dp),
                                imageVector = Icons.Default.Done,
                                colorFilter = ColorFilter.tint(Color(0xFF60E76D)),
                                contentDescription = "Done"
                            )
                        }
                    }
                }
            }
        }
    }
}