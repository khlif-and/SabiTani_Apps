package tech.sabitani.feature.lock.presentation.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.core.security.biometric.BiometricPromptText
import tech.sabitani.core.security.pin.VerifyPinResult
import tech.sabitani.feature.lock.domain.usecase.ObserveLockStatusUseCase
import tech.sabitani.feature.lock.domain.usecase.UnlockBiometricResult
import tech.sabitani.feature.lock.domain.usecase.UnlockWithBiometricUseCase
import tech.sabitani.feature.lock.domain.usecase.VerifyPinUseCase
import tech.sabitani.feature.lock.presentation.state.UnlockEffect
import tech.sabitani.feature.lock.presentation.state.UnlockIntent
import tech.sabitani.feature.lock.presentation.state.UnlockState
import javax.inject.Inject

private const val MAX_PIN_LENGTH = 8

@HiltViewModel
class UnlockViewModel
    @Inject
    constructor(
        private val verifyPinUseCase: VerifyPinUseCase,
        private val observeLockStatusUseCase: ObserveLockStatusUseCase,
        private val unlockWithBiometricUseCase: UnlockWithBiometricUseCase,
    ) : ViewModel(),
        ContainerHost<UnlockState, UnlockEffect> {
        override val container =
            container<UnlockState, UnlockEffect>(UnlockState()) {
                val status = observeLockStatusUseCase()
                reduce {
                    state.copy(
                        isBiometricEnabled = status.isBiometricEnabled,
                        isBiometricAvailable = status.isBiometricAvailable,
                    )
                }
                if (status.isBiometricEnabled && status.isBiometricAvailable) {
                    postSideEffect(UnlockEffect.TriggerBiometricPrompt)
                }
            }

        fun onIntent(intent: UnlockIntent) {
            when (intent) {
                is UnlockIntent.PinChanged -> reducePin(intent.value)
                UnlockIntent.SubmitClicked -> submit()
                UnlockIntent.BiometricRequested -> intent { postSideEffect(UnlockEffect.TriggerBiometricPrompt) }
            }
        }

        fun runBiometricUnlock(activity: FragmentActivity) =
            intent {
                val result =
                    unlockWithBiometricUseCase(
                        activity = activity,
                        prompt =
                            BiometricPromptText(
                                title = "Buka kunci",
                                subtitle = "Verifikasi biometrik untuk membuka aplikasi",
                                negativeButton = "Gunakan PIN",
                            ),
                    )
                when (result) {
                    UnlockBiometricResult.Success -> postSideEffect(UnlockEffect.Unlocked)
                    UnlockBiometricResult.Failed ->
                        reduce { state.copy(errorMessage = "Verifikasi biometrik gagal") }
                    UnlockBiometricResult.Cancelled,
                    UnlockBiometricResult.NotEnrolled,
                    -> Unit
                }
            }

        private fun reducePin(value: String) =
            intent {
                if (value.length <= MAX_PIN_LENGTH && value.all(Char::isDigit)) {
                    reduce { state.copy(pin = value, errorMessage = null) }
                }
            }

        private fun submit() =
            intent {
                val pin = state.pin
                if (pin.isEmpty()) return@intent
                reduce { state.copy(isSubmitting = true, errorMessage = null) }
                val result = verifyPinUseCase(pin.toCharArray())
                reduce { state.copy(isSubmitting = false) }
                when (result) {
                    VerifyPinResult.Success -> {
                        reduce { state.copy(pin = "") }
                        postSideEffect(UnlockEffect.Unlocked)
                    }
                    VerifyPinResult.Incorrect ->
                        reduce {
                            state.copy(pin = "", errorMessage = "PIN salah")
                        }
                    VerifyPinResult.PinNotConfigured -> postSideEffect(UnlockEffect.Unlocked)
                }
            }
    }
