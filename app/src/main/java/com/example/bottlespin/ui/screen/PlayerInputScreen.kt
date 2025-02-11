package com.example.bottlespin.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bottlespin.R

@Composable
fun PlayerInputScreen(onStartGame: (List<String>) -> Unit) {
    var players by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Splash Screen Text
        Text(
            text = "Lets Have Fun!",
            fontSize = 30.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.bottle),
            contentDescription = "Bottle",
            modifier = Modifier
                .size(250.dp), // Set a fixed size for the image
            contentScale = androidx.compose.ui.layout.ContentScale.Fit // Ensure the image fits within the fixed size
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            shape = CircleShape,
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Player Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedBorderColor = Color.Blue, // Blue when focused
                unfocusedBorderColor = Color.Gray // Gray when not focused
            ),
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .background(Color.Transparent)
                .onFocusChanged { focusState -> isFocused = focusState.isFocused } // Only needed if you want extra logic on focus
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row {
            Button(
                onClick = {
                    when {
                        inputText.text.isEmpty() -> errorMessage = "Player name cannot be empty"
                        players.size >= 10 -> errorMessage = "Cannot add more than 10 players"
                        else -> {
                            players = players + inputText.text
                            inputText = TextFieldValue()
                            errorMessage = null
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue) // ✅ Correct way
            ) {
                Text(text = "Add Player")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (players.size < 4) {
                        errorMessage = "At least 4 players are required"
                    } else {
                        errorMessage = null
                        onStartGame(players)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green) // Optional: Custom color for second button
            ) {
                Text(text = "Start Game")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }
    }
}
