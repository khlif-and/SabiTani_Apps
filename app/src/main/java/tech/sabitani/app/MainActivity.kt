package tech.sabitani.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.sabitani.app.navigation.SabiTaniAppNavGraph
import tech.sabitani.core.designsystem.theme.SabiTaniTheme
import tech.sabitani.core.security.pin.PinManager
import tech.sabitani.feature.lock.presentation.screen.UnlockRoute
import tech.sabitani.feature.splash.presentation.screen.SplashRoute
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination: Any = if (pinManager.isPinEnabled()) UnlockRoute else SplashRoute
        setContent {
            SabiTaniTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    val navController = rememberNavController()
                    SabiTaniAppNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
