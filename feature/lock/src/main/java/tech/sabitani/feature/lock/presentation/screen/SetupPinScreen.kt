package tech.sabitani.feature.lock.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.feature.lock.presentation.state.SetupPinEffect
import tech.sabitani.feature.lock.presentation.state.SetupPinIntent
import tech.sabitani.feature.lock.presentation.viewmodel.SetupPinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SetupPinScreen(
    onBack: () -> Unit,
    onPinConfigured: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SetupPinViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SetupPinEffect.PinConfigured -> onPinConfigured()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Atur PIN") },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Buat PIN 4–8 digit untuk mengunci aplikasi.",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                value = state.pin,
                onValueChange = { viewModel.onIntent(SetupPinIntent.PinChanged(it)) },
                label = { Text("PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.confirmPin,
                onValueChange = { viewModel.onIntent(SetupPinIntent.ConfirmPinChanged(it)) },
                label = { Text("Konfirmasi PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
            )
            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                onClick = { viewModel.onIntent(SetupPinIntent.SubmitClicked) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                }
                Text("Aktifkan PIN")
            }
        }
    }
}

fun NavGraphBuilder.setupPinScreen(
    onBack: () -> Unit,
    onPinConfigured: () -> Unit,
) {
    composable<SetupPinRoute> {
        SetupPinScreen(
            onBack = onBack,
            onPinConfigured = onPinConfigured,
        )
    }
}
