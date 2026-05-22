package tech.sabitani.core.security.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class DefaultBiometricAuthenticator
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : BiometricAuthenticator {
        private val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG

        override fun status(): BiometricStatus =
            when (BiometricManager.from(context).canAuthenticate(authenticators)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                -> BiometricStatus.HARDWARE_UNAVAILABLE
                else -> BiometricStatus.UNSUPPORTED
            }

        override suspend fun authenticate(
            activity: FragmentActivity,
            prompt: BiometricPromptText,
            cryptoObject: BiometricPrompt.CryptoObject?,
        ): BiometricResult =
            suspendCancellableCoroutine { continuation ->
                val executor = ContextCompat.getMainExecutor(activity)
                val biometricPrompt =
                    BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                if (continuation.isActive) {
                                    continuation.resume(BiometricResult.Success(result.cryptoObject))
                                }
                            }

                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence,
                            ) {
                                if (!continuation.isActive) return
                                val mapped =
                                    when (errorCode) {
                                        BiometricPrompt.ERROR_USER_CANCELED,
                                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                        BiometricPrompt.ERROR_CANCELED,
                                        -> BiometricResult.UserCancelled
                                        else -> BiometricResult.Error(code = errorCode, message = errString.toString())
                                    }
                                continuation.resume(mapped)
                            }
                        },
                    )

                val info =
                    BiometricPrompt.PromptInfo
                        .Builder()
                        .setTitle(prompt.title)
                        .setSubtitle(prompt.subtitle)
                        .setNegativeButtonText(prompt.negativeButton)
                        .setAllowedAuthenticators(authenticators)
                        .build()

                if (cryptoObject != null) {
                    biometricPrompt.authenticate(info, cryptoObject)
                } else {
                    biometricPrompt.authenticate(info)
                }

                continuation.invokeOnCancellation { biometricPrompt.cancelAuthentication() }
            }
    }
