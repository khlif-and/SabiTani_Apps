package tech.sabitani.feature.lock.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.feature.lock.presentation.state.UnlockEffect
import tech.sabitani.feature.lock.presentation.state.UnlockIntent
import tech.sabitani.feature.lock.presentation.viewmodel.UnlockViewModel

@Composable
internal fun UnlockScreen(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UnlockViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? FragmentActivity

    viewModel.collectSideEffect { effect ->
        when (effect) {
            UnlockEffect.Unlocked -> onUnlocked()
            UnlockEffect.TriggerBiometricPrompt -> activity?.let { viewModel.runBiometricUnlock(it) }
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Masukkan PIN",
            style = MaterialTheme.typography.headlineMedium,
        )
        OutlinedTextField(
            value = state.pin,
            onValueChange = { viewModel.onIntent(UnlockIntent.PinChanged(it)) },
            label = { Text("PIN") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
        )
        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Button(
            onClick = { viewModel.onIntent(UnlockIntent.SubmitClicked) },
            enabled = !state.isSubmitting && state.pin.isNotEmpty(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            }
            Text("Buka")
        }
        if (state.isBiometricEnabled && state.isBiometricAvailable) {
            TextButton(
                onClick = { viewModel.onIntent(UnlockIntent.BiometricRequested) },
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text("Gunakan biometrik")
            }
        }
    }
}

fun NavGraphBuilder.unlockScreen(onUnlocked: () -> Unit) {
    composable<UnlockRoute> {
        UnlockScreen(onUnlocked = onUnlocked)
    }
}
