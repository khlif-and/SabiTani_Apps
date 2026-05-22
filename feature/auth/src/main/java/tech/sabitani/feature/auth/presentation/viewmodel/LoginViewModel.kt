package tech.sabitani.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.auth.domain.usecase.LoginUseCase
import tech.sabitani.feature.auth.presentation.state.LoginEffect
import tech.sabitani.feature.auth.presentation.state.LoginIntent
import tech.sabitani.feature.auth.presentation.state.LoginState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
    ) : ViewModel(),
        ContainerHost<LoginState, LoginEffect> {
        override val container = container<LoginState, LoginEffect>(LoginState())

        fun onIntent(intent: LoginIntent) {
            when (intent) {
                is LoginIntent.EmailChanged -> reduceEmail(intent.value)
                is LoginIntent.PasswordChanged -> reducePassword(intent.value)
                LoginIntent.SubmitClicked -> submit()
            }
        }

        private fun reduceEmail(value: String) =
            intent {
                reduce { state.copy(email = value) }
            }

        private fun reducePassword(value: String) =
            intent {
                reduce { state.copy(password = value) }
            }

        private fun submit() =
            intent {
                reduce { state.copy(isSubmitting = true) }
                val result = loginUseCase(state.email, state.password)
                reduce { state.copy(isSubmitting = false) }
                result.fold(
                    onSuccess = { postSideEffect(LoginEffect.LoginSucceeded) },
                    onFailure = { postSideEffect(LoginEffect.ShowError(it.message ?: "Login gagal")) },
                )
            }
    }
