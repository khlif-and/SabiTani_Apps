package tech.sabitani.feature.lock.domain.usecase

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import tech.sabitani.core.security.biometric.BiometricAuthenticator
import tech.sabitani.core.security.biometric.BiometricDekUnwrapper
import tech.sabitani.core.security.biometric.BiometricPromptText
import tech.sabitani.core.security.biometric.BiometricResult
import tech.sabitani.core.security.lock.LockSettingsRepository
import tech.sabitani.core.security.pin.PinManager
import javax.inject.Inject

class EnableBiometricUseCase
    @Inject
    constructor(
        private val pinManager: PinManager,
        private val biometricAuthenticator: BiometricAuthenticator,
        private val biometricDekUnwrapper: BiometricDekUnwrapper,
        private val lockSettingsRepository: LockSettingsRepository,
    ) {
        suspend operator fun invoke(
            activity: FragmentActivity,
            prompt: BiometricPromptText,
        ): EnableBiometricResult {
            if (!pinManager.isPinEnabled()) return EnableBiometricResult.PinNotConfigured
            val cipher = biometricDekUnwrapper.prepareEnrolCipher()
            val result =
                biometricAuthenticator.authenticate(
                    activity = activity,
                    prompt = prompt,
                    cryptoObject = BiometricPrompt.CryptoObject(cipher),
                )
            return when (result) {
                is BiometricResult.Success -> {
                    val unlockedCipher = result.cryptoObject?.cipher ?: return EnableBiometricResult.Failed
                    biometricDekUnwrapper.enrol(unlockedCipher)
                    lockSettingsRepository.setBiometricEnabled(true)
                    EnableBiometricResult.Success
                }
                BiometricResult.UserCancelled -> EnableBiometricResult.Cancelled
                is BiometricResult.Error -> EnableBiometricResult.Failed
            }
        }
    }

sealed interface EnableBiometricResult {
    data object Success : EnableBiometricResult

    data object Cancelled : EnableBiometricResult

    data object Failed : EnableBiometricResult

    data object PinNotConfigured : EnableBiometricResult
}
