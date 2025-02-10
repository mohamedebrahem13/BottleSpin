package com.example.bottlespin.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
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

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "Bottle Rotation"
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

    // Start winner animation AFTER bottle stops
    LaunchedEffect(animatedRotation) {
        if (rotation == animatedRotation) {
            winnerAnimationEnabled = true
            delay(4000) // Stop animation after 4 seconds
            winnerAnimationEnabled = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Current Turn: $winner", fontSize = 24.sp, color = Color.Black)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            players.forEachIndexed { index, name ->
                val angle = (2 * PI / players.size) * index
                val safeRadius = 140

                val isWinner = index == winnerIndex

                // 🎨 Animate winner's color only when enabled
                val animatedColor by animateColorAsState(
                    targetValue = if (isWinner && winnerAnimationEnabled) Color.Yellow else colors[index],
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "Winner Color Animation"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = (safeRadius * cos(angle)).toFloat().dp,
                            y = (safeRadius * sin(angle)).toFloat().dp
                        )
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.bottle),
                    contentDescription = "Spinning Bottle",
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer(rotationZ = animatedRotation)
                        .clickable {
                            if (rotation == animatedRotation) { // Prevent clicking while spinning
                                winnerAnimationEnabled = false // Reset winner animation
                                rotation += Random.nextFloat() * 3600
                            }
                        }
                )
            }
        }
    }
}
