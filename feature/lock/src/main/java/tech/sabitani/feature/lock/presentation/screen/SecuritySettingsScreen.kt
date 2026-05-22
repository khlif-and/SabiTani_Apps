package tech.sabitani.feature.lock.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import tech.sabitani.feature.lock.presentation.state.SecuritySettingsEffect
import tech.sabitani.feature.lock.presentation.state.SecuritySettingsIntent
import tech.sabitani.feature.lock.presentation.viewmodel.SecuritySettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SecuritySettingsScreen(
    onBack: () -> Unit,
    onNavigateToSetupPin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SecuritySettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = LocalContext.current as? FragmentActivity

    LaunchedEffect(Unit) { viewModel.refreshStatus() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SecuritySettingsEffect.NavigateToSetupPin -> onNavigateToSetupPin()
            is SecuritySettingsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Keamanan") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ToggleRow(
                title = "PIN Aplikasi",
                description = "Kunci aplikasi dengan PIN 4–8 digit.",
                checked = state.isPinEnabled,
                enabled = !state.isProcessing,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        viewModel.onIntent(SecuritySettingsIntent.EnablePinClicked)
                    } else {
                        viewModel.onIntent(SecuritySettingsIntent.DisablePinRequested)
                    }
                },
            )
            HorizontalDivider()
            ToggleRow(
                title = "Unlock Biometrik",
                description =
                    when {
                        !state.isPinEnabled -> "Aktifkan PIN dulu untuk menggunakan biometrik."
                        !state.isBiometricAvailable -> "Biometrik tidak tersedia di perangkat ini."
                        else -> "Gunakan sidik jari/wajah untuk buka aplikasi."
                    },
                checked = state.isBiometricEnabled,
                enabled = state.isPinEnabled && state.isBiometricAvailable && !state.isProcessing,
                onCheckedChange = { enabled ->
                    viewModel.onIntent(
                        intent = SecuritySettingsIntent.BiometricToggled(enabled = enabled),
                        activity = activity,
                    )
                },
            )
        }
    }

    if (state.showDisableDialog) {
        DisablePinDialog(
            pinInput = state.disablePinInput,
            errorMessage = state.errorMessage,
            isProcessing = state.isProcessing,
            onPinChange = { viewModel.onIntent(SecuritySettingsIntent.DisablePinInputChanged(it)) },
            onConfirm = { viewModel.onIntent(SecuritySettingsIntent.DisablePinConfirmed) },
            onDismiss = { viewModel.onIntent(SecuritySettingsIntent.DisableDialogDismissed) },
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun DisablePinDialog(
    pinInput: String,
    errorMessage: String?,
    isProcessing: Boolean,
    onPinChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Matikan PIN") },
        text = {
            Column {
                Text("Masukkan PIN saat ini untuk mematikan kunci PIN.")
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = onPinChange,
                    label = { Text("PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.padding(top = 12.dp),
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isProcessing && pinInput.isNotEmpty(),
            ) {
                Text("Matikan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isProcessing) {
                Text("Batal")
            }
        },
    )
}

fun NavGraphBuilder.securitySettingsScreen(
    onBack: () -> Unit,
    onNavigateToSetupPin: () -> Unit,
) {
    composable<SecuritySettingsRoute> {
        SecuritySettingsScreen(
            onBack = onBack,
            onNavigateToSetupPin = onNavigateToSetupPin,
        )
    }
}
