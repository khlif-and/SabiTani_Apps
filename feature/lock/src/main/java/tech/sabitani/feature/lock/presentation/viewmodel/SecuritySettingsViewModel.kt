package tech.sabitani.feature.lock.presentation.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.core.analytics.consent.AnalyticsConsent
import tech.sabitani.core.security.biometric.BiometricPromptText
import tech.sabitani.feature.lock.domain.usecase.DisableBiometricUseCase
import tech.sabitani.feature.lock.domain.usecase.DisablePinUseCase
import tech.sabitani.feature.lock.domain.usecase.ObserveLockStatusUseCase
import tech.sabitani.feature.lock.presentation.state.SecuritySettingsEffect
import tech.sabitani.feature.lock.presentation.state.SecuritySettingsIntent
import tech.sabitani.feature.lock.presentation.state.SecuritySettingsState
import tech.sabitani.feature.lock.presentation.usecase.EnableBiometricResult
import tech.sabitani.feature.lock.presentation.usecase.EnableBiometricUseCase
import javax.inject.Inject

@HiltViewModel
class SecuritySettingsViewModel
    @Inject
    constructor(
        private val observeLockStatusUseCase: ObserveLockStatusUseCase,
        private val disablePinUseCase: DisablePinUseCase,
        private val enableBiometricUseCase: EnableBiometricUseCase,
        private val disableBiometricUseCase: DisableBiometricUseCase,
        private val analyticsConsent: AnalyticsConsent,
    ) : ViewModel(),
        ContainerHost<SecuritySettingsState, SecuritySettingsEffect> {
        override val container =
            container<SecuritySettingsState, SecuritySettingsEffect>(SecuritySettingsState()) {
                refreshStatus()
            }

        fun onIntent(
            intent: SecuritySettingsIntent,
            activity: FragmentActivity? = null,
        ) {
            when (intent) {
                SecuritySettingsIntent.EnablePinClicked -> handleEnablePin()
                SecuritySettingsIntent.DisablePinRequested -> openDisableDialog()
                SecuritySettingsIntent.DisableDialogDismissed -> closeDisableDialog()
                is SecuritySettingsIntent.DisablePinInputChanged -> reduceDisableInput(intent.value)
                SecuritySettingsIntent.DisablePinConfirmed -> handleDisablePin()
                is SecuritySettingsIntent.BiometricToggled ->
                    handleBiometricToggle(enabled = intent.enabled, activity = activity)
                is SecuritySettingsIntent.AnalyticsConsentToggled ->
                    handleAnalyticsToggle(enabled = intent.enabled)
            }
        }

        fun refreshStatus() =
            intent {
                val status = observeLockStatusUseCase()
                val analyticsEnabled = analyticsConsent.isEnabledNow()
                reduce {
                    state.copy(
                        isPinEnabled = status.isPinEnabled,
                        isBiometricEnabled = status.isBiometricEnabled,
                        isBiometricAvailable = status.isBiometricAvailable,
                        isAnalyticsEnabled = analyticsEnabled,
                    )
                }
            }

        private fun handleEnablePin() =
            intent {
                postSideEffect(SecuritySettingsEffect.NavigateToSetupPin)
            }

        private fun openDisableDialog() =
            intent {
                reduce { state.copy(showDisableDialog = true, disablePinInput = "", errorMessage = null) }
            }

        private fun closeDisableDialog() =
            intent {
                reduce { state.copy(showDisableDialog = false, disablePinInput = "") }
            }

        private fun reduceDisableInput(value: String) =
            intent {
                if (value.length <= MAX_PIN_LENGTH && value.all(Char::isDigit)) {
                    reduce { state.copy(disablePinInput = value) }
                }
            }

        private fun handleDisablePin() =
            intent {
                val pin = state.disablePinInput
                if (pin.isEmpty()) return@intent
                reduce { state.copy(isProcessing = true, errorMessage = null) }
                val ok = disablePinUseCase(pin.toCharArray())
                reduce {
                    state.copy(
                        isProcessing = false,
                        showDisableDialog = !ok,
                        disablePinInput = if (ok) "" else state.disablePinInput,
                        errorMessage = if (ok) null else "PIN salah",
                    )
                }
                if (ok) {
                    postSideEffect(SecuritySettingsEffect.ShowMessage("PIN dinonaktifkan"))
                    refreshStatus()
                }
            }

        private fun handleBiometricToggle(
            enabled: Boolean,
            activity: FragmentActivity?,
        ) = intent {
            if (enabled && activity != null) {
                reduce { state.copy(isProcessing = true) }
                val result =
                    enableBiometricUseCase(
                        activity = activity,
                        prompt =
                            BiometricPromptText(
                                title = "Aktifkan biometrik",
                                subtitle = "Verifikasi identitas untuk mengaktifkan unlock biometrik",
                                negativeButton = "Batal",
                            ),
                    )
                reduce { state.copy(isProcessing = false) }
                when (result) {
                    EnableBiometricResult.Success -> postSideEffect(SecuritySettingsEffect.ShowMessage("Biometrik aktif"))
                    EnableBiometricResult.Cancelled -> Unit
                    EnableBiometricResult.Failed -> postSideEffect(SecuritySettingsEffect.ShowMessage("Aktivasi biometrik gagal"))
                    EnableBiometricResult.PinNotConfigured ->
                        postSideEffect(SecuritySettingsEffect.ShowMessage("Aktifkan PIN terlebih dahulu"))
                }
                refreshStatus()
            } else if (!enabled) {
                disableBiometricUseCase()
                refreshStatus()
                postSideEffect(SecuritySettingsEffect.ShowMessage("Biometrik dimatikan"))
            }
        }

        private fun handleAnalyticsToggle(enabled: Boolean) =
            intent {
                analyticsConsent.setEnabled(enabled)
                reduce { state.copy(isAnalyticsEnabled = enabled) }
                val message =
                    if (enabled) {
                        "Analitik anonim diaktifkan"
                    } else {
                        "Analitik anonim dimatikan"
                    }
                postSideEffect(SecuritySettingsEffect.ShowMessage(message))
            }

        private companion object {
            const val MAX_PIN_LENGTH = 8
        }
    }
