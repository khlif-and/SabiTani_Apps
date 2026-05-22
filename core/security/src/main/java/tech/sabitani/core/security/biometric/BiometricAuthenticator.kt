package tech.sabitani.core.security.biometric

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

interface BiometricAuthenticator {
    fun status(): BiometricStatus

    suspend fun authenticate(
        activity: FragmentActivity,
        prompt: BiometricPromptText,
        cryptoObject: BiometricPrompt.CryptoObject? = null,
    ): BiometricResult
}

enum class BiometricStatus {
    AVAILABLE,
    NOT_ENROLLED,
    HARDWARE_UNAVAILABLE,
    UNSUPPORTED,
}

sealed interface BiometricResult {
    data class Success(
        val cryptoObject: BiometricPrompt.CryptoObject?,
    ) : BiometricResult

    data object UserCancelled : BiometricResult

    data class Error(
        val code: Int,
        val message: String,
    ) : BiometricResult
}

data class BiometricPromptText(
    val title: String,
    val subtitle: String,
    val negativeButton: String,
)
