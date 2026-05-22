package tech.sabitani.feature.lock.domain.usecase

import tech.sabitani.core.security.lock.LockSettingsRepository
import tech.sabitani.core.security.pin.PinManager
import javax.inject.Inject

class DisablePinUseCase
    @Inject
    constructor(
        private val pinManager: PinManager,
        private val lockSettingsRepository: LockSettingsRepository,
    ) {
        suspend operator fun invoke(currentPin: CharArray): Boolean {
            val disabled = pinManager.disablePin(currentPin)
            if (disabled) {
                lockSettingsRepository.setBiometricEnabled(false)
            }
            return disabled
        }
    }
