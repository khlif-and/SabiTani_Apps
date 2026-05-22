package tech.sabitani.feature.splash.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1_500L

@Composable
internal fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onTimeout()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "SabiTani",
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}

fun NavGraphBuilder.splashScreen(onTimeout: () -> Unit) {
    composable<SplashRoute> {
        SplashScreen(onTimeout = onTimeout)
    }
}
