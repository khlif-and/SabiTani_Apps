package tech.sabitani.feature.onboarding.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tech.sabitani.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@Composable
internal fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Selamat datang di SabiTani",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Platform pengetahuan untuk petani modern.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp),
        )
        Button(onClick = { viewModel.onGetStartedClicked(onFinished) }) {
            Text("Mulai")
        }
    }
}

fun NavGraphBuilder.onboardingScreen(onFinished: () -> Unit) {
    composable<OnboardingRoute> {
        OnboardingScreen(onFinished = onFinished)
    }
}
