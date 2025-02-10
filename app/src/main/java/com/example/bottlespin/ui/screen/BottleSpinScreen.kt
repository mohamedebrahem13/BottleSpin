package com.example.bottlespin.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bottlespin.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BottleSpinScreen(players: List<String>) {
    var rotation by remember { mutableFloatStateOf(0f) }
    var winnerAnimationEnabled by remember { mutableStateOf(false) }
    var lastWinnerIndex by remember { mutableIntStateOf(-1) } // ✅ Track last winner

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "Bottle Rotation"
    )
    // Scale animation for the winner text
    val scale by animateFloatAsState(
        targetValue = if (winnerAnimationEnabled) 1.2f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "Winner Scale Animation"
    )


    val winnerIndex by remember {
        derivedStateOf {
            val bottleHeadOffset = 270f
            val adjustedRotation = (animatedRotation + bottleHeadOffset) % 360

            val sectionSize = 360f / players.size
            val baseIndex = (adjustedRotation / sectionSize).toInt() % players.size

            val nextIndex = (baseIndex + 1) % players.size

            val baseAngle = baseIndex * sectionSize
            val nextAngle = nextIndex * sectionSize

            val distanceToBase = kotlin.math.abs(adjustedRotation - baseAngle)
            val distanceToNext = kotlin.math.abs(adjustedRotation - nextAngle)

            if (distanceToBase < distanceToNext) baseIndex else nextIndex
        }
    }

    val winner = players[winnerIndex]

    val colors = remember {
        players.map { Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f) }
    }

    // 🟡 Winner animation handler
    LaunchedEffect(animatedRotation) {
        if (rotation == animatedRotation) { // ✅ When bottle stops
            lastWinnerIndex = winnerIndex // ✅ Save last winner
            winnerAnimationEnabled = true
            delay(4000) // ⏳ Stop animation after 4 seconds
            winnerAnimationEnabled = false
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val safeRadius = 140.dp  // 🟢 The same radius used for player placement
        val textOffset = safeRadius + 70.dp // ✅ Position above the topmost player

        // 🟢 Winner text positioned above the topmost player
        AnimatedContent(
            targetState = winner,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)).togetherWith(
                    fadeOut(animationSpec = tween(500))
                )
            },
            label = "Winner Change Animation",
            modifier = Modifier
                .offset(y = -textOffset) // ✅ Moves above the highest player dynamically
                .fillMaxWidth()
                .graphicsLayer(scaleX = scale, scaleY = scale) // 🔥 Apply scale animation
        ) { targetWinner ->
            Text(
                text = "Current Turn: $targetWinner",
                fontSize = 35.sp,
                textAlign = TextAlign.Center
            )
        }

        // 🟢 Players and bottle stay in the center
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            players.forEachIndexed { index, name ->
                val angle = (2 * PI / players.size) * index
                val playerSafeRadius  = 140.dp

                val xOffset = (playerSafeRadius .value * cos(angle)).dp
                val yOffset = (playerSafeRadius .value * sin(angle)).dp

                val isWinner = index == lastWinnerIndex

                val animatedColor by animateColorAsState(
                    targetValue = if (isWinner && winnerAnimationEnabled) Color.Yellow else colors[index],
                    animationSpec = if (isWinner && winnerAnimationEnabled) {
                        infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse)
                    } else {
                        tween(500)
                    },
                    label = "Winner Color Animation"
                )
                Box(
                    modifier = Modifier
                        .offset(x = xOffset, y = yOffset)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(animatedColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.bottle),
                contentDescription = "Spinning Bottle",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(rotationZ = animatedRotation)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (rotation == animatedRotation) {
                            winnerAnimationEnabled = false
                            lastWinnerIndex = -1
                            rotation += Random.nextFloat() * 3600
                        }
                    }
            )
        }
    }
}