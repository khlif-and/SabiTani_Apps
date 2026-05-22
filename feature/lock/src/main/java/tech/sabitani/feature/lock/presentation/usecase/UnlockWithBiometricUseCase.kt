package tech.sabitani.feature.lock.presentation.usecase

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import tech.sabitani.core.security.biometric.BiometricAuthenticator
import tech.sabitani.core.security.biometric.BiometricDekUnwrapper
import tech.sabitani.core.security.biometric.BiometricPromptText
import tech.sabitani.core.security.biometric.BiometricResult
import javax.inject.Inject

class UnlockWithBiometricUseCase
    @Inject
    constructor(
        private val biometricAuthenticator: BiometricAuthenticator,
        private val biometricDekUnwrapper: BiometricDekUnwrapper,
    ) {
        suspend operator fun invoke(
            activity: FragmentActivity,
            prompt: BiometricPromptText,
        ): UnlockBiometricResult {
            val cipher = biometricDekUnwrapper.prepareUnlockCipher() ?: return UnlockBiometricResult.NotEnrolled
            val result =
                biometricAuthenticator.authenticate(
                    activity = activity,
                    prompt = prompt,
                    cryptoObject = BiometricPrompt.CryptoObject(cipher),
                )
            return when (result) {
                is BiometricResult.Success -> {
                    val unlockedCipher = result.cryptoObject?.cipher ?: return UnlockBiometricResult.Failed
                    val unlocked = biometricDekUnwrapper.unlockWithCipher(unlockedCipher)
                    if (unlocked) UnlockBiometricResult.Success else UnlockBiometricResult.Failed
                }
                BiometricResult.UserCancelled -> UnlockBiometricResult.Cancelled
                is BiometricResult.Error -> UnlockBiometricResult.Failed
            }
        }
    }

sealed interface UnlockBiometricResult {
    data object Success : UnlockBiometricResult

    data object Cancelled : UnlockBiometricResult

    data object Failed : UnlockBiometricResult

    data object NotEnrolled : UnlockBiometricResult
}
