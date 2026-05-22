package tech.sabitani.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.sabitani.app.navigation.SabiTaniAppNavGraph
import tech.sabitani.core.designsystem.theme.SabiTaniTheme
import tech.sabitani.feature.splash.presentation.screen.SplashRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SabiTaniTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val navController = rememberNavController()
                    SabiTaniAppNavGraph(
                        navController = navController,
                        startDestination = SplashRoute,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
