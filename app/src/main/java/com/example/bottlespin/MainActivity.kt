package com.example.bottlespin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bottlespin.ui.screen.BottleSpinScreen
import com.example.bottlespin.ui.screen.PlayerInputScreen
import com.example.bottlespin.ui.theme.BottleSpinTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            BottleSpinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(innerPadding)
                }
            }
        }
    }
}
@Composable
fun AppNavHost(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = PickPlayer, modifier = Modifier.padding(innerPadding)) {
        composable<PickPlayer>{
            PlayerInputScreen { players ->
                navController.navigate(Bottle(players))
            }

        }
        composable<Bottle> { backStackEntry ->
            val players = backStackEntry.toRoute<Bottle>()
            BottleSpinScreen(players.players)
        }
    }

}
@Serializable
data object PickPlayer

@Serializable
data class Bottle (val players: List<String>)
