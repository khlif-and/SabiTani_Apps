package tech.sabitani.feature.lock.domain.usecase

import tech.sabitani.core.security.biometric.BiometricAuthenticator
import tech.sabitani.core.security.biometric.BiometricStatus
import tech.sabitani.core.security.lock.LockSettingsRepository
import tech.sabitani.core.security.pin.PinManager
import javax.inject.Inject

class ObserveLockStatusUseCase
    @Inject
    constructor(
        private val pinManager: PinManager,
        private val lockSettingsRepository: LockSettingsRepository,
        private val biometricAuthenticator: BiometricAuthenticator,
    ) {
        operator fun invoke(): LockStatus =
            LockStatus(
                isPinEnabled = pinManager.isPinEnabled(),
                isBiometricEnabled = lockSettingsRepository.isBiometricEnabled(),
                isBiometricAvailable = biometricAuthenticator.status() == BiometricStatus.AVAILABLE,
            )
    }

data class LockStatus(
    val isPinEnabled: Boolean,
    val isBiometricEnabled: Boolean,
    val isBiometricAvailable: Boolean,
)
