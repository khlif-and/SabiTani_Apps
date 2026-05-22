package tech.sabitani.feature.lock.domain.usecase

import tech.sabitani.core.security.biometric.BiometricDekUnwrapper
import tech.sabitani.core.security.lock.LockSettingsRepository
import javax.inject.Inject

class DisableBiometricUseCase
    @Inject
    constructor(
        private val biometricDekUnwrapper: BiometricDekUnwrapper,
        private val lockSettingsRepository: LockSettingsRepository,
    ) {
        operator fun invoke() {
            biometricDekUnwrapper.clear()
            lockSettingsRepository.setBiometricEnabled(false)
        }
    }
