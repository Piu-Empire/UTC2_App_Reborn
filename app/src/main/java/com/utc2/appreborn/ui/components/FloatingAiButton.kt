package com.utc2.appreborn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utc2.appreborn.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.roundToInt

@Composable
fun FloatingAiButton(
    modifier: Modifier = Modifier,
    onAiChatClick: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var hasInitialized by remember { mutableStateOf(false) }

    var containerWidth by remember { mutableIntStateOf(0) }
    var containerHeight by remember { mutableIntStateOf(0) }
    val buttonSize = 64.dp
    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }

    val coroutineScope = rememberCoroutineScope()
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Pulsing animation for the AI orb
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val colorStart = Color(0xFFFFF59D) // Light Yellow
    val colorEnd = Color(0xFF81D4FA)   // Light Blue

    var isIdle by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX, offsetY, isMenuExpanded) {
        isIdle = false
        delay(5000)
        if (!isMenuExpanded && offsetX != 0f) {
            isIdle = true
        }
    }

    val idleAlpha by animateFloatAsState(targetValue = if (isIdle) 0.4f else 1f, label = "idleAlpha")
    val idleTranslationX by animateFloatAsState(
        targetValue = if (isIdle) {
            val center = containerWidth / 2f
            if (offsetX + buttonSizePx / 2 < center) -buttonSizePx / 1.5f else buttonSizePx / 1.5f
        } else 0f,
        label = "idleTranslationX"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width
                containerHeight = coordinates.size.height
                // Initial position: Bottom Right
                if (offsetX == 0f && offsetY == 0f && containerWidth > 0 && !hasInitialized) {
                    offsetX = containerWidth - buttonSizePx - 32f // 16dp margin
                    offsetY = containerHeight - buttonSizePx - 200f // Above bottom bar
                    hasInitialized = true
                }
            }
    ) {
        if (containerWidth > 0 && containerHeight > 0) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .graphicsLayer {
                        translationX = idleTranslationX
                        alpha = idleAlpha
                    }
                    .size(buttonSize)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(colorStart, colorEnd)))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                // Snap to edge
                                val center = containerWidth / 2f
                                val targetX = if (offsetX + buttonSizePx / 2 < center) {
                                    16f // Snap to left with padding
                                } else {
                                    containerWidth - buttonSizePx - 16f // Snap to right with padding
                                }
                                coroutineScope.launch {
                                    // We could use Animatable for smooth snapping, 
                                    // but for simplicity, we directly set or animate it.
                                    Animatable(offsetX).animateTo(
                                        targetValue = targetX,
                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                    ) {
                                        offsetX = value
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, containerWidth - buttonSizePx)
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, containerHeight - buttonSizePx)
                        }
                    }
                    .clickable { onAiChatClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lucide_bot),
                    contentDescription = "AI Assistant",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

fun setupFloatingAiButton(
    composeView: androidx.compose.ui.platform.ComposeView,
    onAiChatClick: () -> Unit
) {
    composeView.setContent {
        MaterialTheme {
            FloatingAiButton(onAiChatClick = onAiChatClick)
        }
    }
}
