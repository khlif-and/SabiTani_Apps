package tech.sabitani.feature.lock.presentation.state

data class SecuritySettingsState(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val disablePinInput: String = "",
    val showDisableDialog: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SecuritySettingsIntent {
    data object EnablePinClicked : SecuritySettingsIntent

    data object DisablePinRequested : SecuritySettingsIntent

    data object DisableDialogDismissed : SecuritySettingsIntent

    data class DisablePinInputChanged(
        val value: String,
    ) : SecuritySettingsIntent

    data object DisablePinConfirmed : SecuritySettingsIntent

    data class BiometricToggled(
        val enabled: Boolean,
    ) : SecuritySettingsIntent
}

sealed interface SecuritySettingsEffect {
    data object NavigateToSetupPin : SecuritySettingsEffect

    data class ShowMessage(
        val message: String,
    ) : SecuritySettingsEffect
}
