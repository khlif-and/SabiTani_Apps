package tech.sabitani.feature.lock.presentation.state

data class SetupPinState(
    val pin: String = "",
    val confirmPin: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SetupPinIntent {
    data class PinChanged(
        val value: String,
    ) : SetupPinIntent

    data class ConfirmPinChanged(
        val value: String,
    ) : SetupPinIntent

    data object SubmitClicked : SetupPinIntent
}

sealed interface SetupPinEffect {
    data object PinConfigured : SetupPinEffect
}
