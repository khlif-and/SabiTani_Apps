package tech.sabitani.feature.lock.presentation.state

data class UnlockState(
    val pin: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
)

sealed interface UnlockIntent {
    data class PinChanged(
        val value: String,
    ) : UnlockIntent

    data object SubmitClicked : UnlockIntent

    data object BiometricRequested : UnlockIntent
}

sealed interface UnlockEffect {
    data object Unlocked : UnlockEffect

    data object TriggerBiometricPrompt : UnlockEffect
}
