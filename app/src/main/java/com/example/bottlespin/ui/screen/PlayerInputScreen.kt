package com.example.bottlespin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun PlayerInputScreen(onStartGame: (List<String>) -> Unit) {
    var players by remember { mutableStateOf(listOf<String>()) }
    var inputText by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter Player Name") },
            modifier = Modifier.background(Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            when {
                inputText.text.isEmpty() -> errorMessage = "Player name cannot be empty"
                players.size >= 10 -> errorMessage = "Cannot add more than 10 players"
                else -> {
                    players = players + inputText.text
                    inputText = TextFieldValue()
                    errorMessage = null
                }
            }
        }) {
            Text(text = "Add Player")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (players.size < 4) {
                errorMessage = "At least 4 players are required"
            } else {
                errorMessage = null
                onStartGame(players)
            }
        }) {
            Text(text = "Start Game")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }
    }
}
