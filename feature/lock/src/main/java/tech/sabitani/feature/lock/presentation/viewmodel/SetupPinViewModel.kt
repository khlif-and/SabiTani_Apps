package tech.sabitani.feature.lock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.core.security.pin.SetupPinResult
import tech.sabitani.feature.lock.domain.usecase.SetupPinUseCase
import tech.sabitani.feature.lock.presentation.state.SetupPinEffect
import tech.sabitani.feature.lock.presentation.state.SetupPinIntent
import tech.sabitani.feature.lock.presentation.state.SetupPinState
import javax.inject.Inject

private const val MIN_PIN_LENGTH = 4
private const val MAX_PIN_LENGTH = 8

@HiltViewModel
class SetupPinViewModel
    @Inject
    constructor(
        private val setupPinUseCase: SetupPinUseCase,
    ) : ViewModel(),
        ContainerHost<SetupPinState, SetupPinEffect> {
        override val container = container<SetupPinState, SetupPinEffect>(SetupPinState())

        fun onIntent(intent: SetupPinIntent) {
            when (intent) {
                is SetupPinIntent.PinChanged -> reducePin(intent.value)
                is SetupPinIntent.ConfirmPinChanged -> reduceConfirmPin(intent.value)
                SetupPinIntent.SubmitClicked -> submit()
            }
        }

        private fun reducePin(value: String) =
            intent {
                if (value.length <= MAX_PIN_LENGTH && value.all(Char::isDigit)) {
                    reduce { state.copy(pin = value, errorMessage = null) }
                }
            }

        private fun reduceConfirmPin(value: String) =
            intent {
                if (value.length <= MAX_PIN_LENGTH && value.all(Char::isDigit)) {
                    reduce { state.copy(confirmPin = value, errorMessage = null) }
                }
            }

        private fun submit() =
            intent {
                val pin = state.pin
                val confirm = state.confirmPin
                if (pin.length < MIN_PIN_LENGTH) {
                    reduce { state.copy(errorMessage = "PIN minimal $MIN_PIN_LENGTH digit") }
                    return@intent
                }
                if (pin != confirm) {
                    reduce { state.copy(errorMessage = "Konfirmasi PIN tidak cocok") }
                    return@intent
                }
                reduce { state.copy(isSubmitting = true, errorMessage = null) }
                val result = setupPinUseCase(pin.toCharArray())
                reduce { state.copy(isSubmitting = false) }
                when (result) {
                    SetupPinResult.Success -> postSideEffect(SetupPinEffect.PinConfigured)
                    SetupPinResult.AlreadyEnabled ->
                        reduce {
                            state.copy(errorMessage = "PIN sudah aktif. Matikan dulu untuk mengganti.")
                        }
                }
            }
    }
