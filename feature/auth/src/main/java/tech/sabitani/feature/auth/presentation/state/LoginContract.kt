package tech.sabitani.feature.auth.presentation.state

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
)

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object SubmitClicked : LoginIntent
}

sealed interface LoginEffect {
    data object LoginSucceeded : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}
