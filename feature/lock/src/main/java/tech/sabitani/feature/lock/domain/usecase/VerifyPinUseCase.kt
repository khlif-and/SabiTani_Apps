package tech.sabitani.feature.lock.domain.usecase

import tech.sabitani.core.security.pin.PinManager
import tech.sabitani.core.security.pin.VerifyPinResult
import javax.inject.Inject

class VerifyPinUseCase
    @Inject
    constructor(
        private val pinManager: PinManager,
    ) {
        suspend operator fun invoke(pin: CharArray): VerifyPinResult = pinManager.verifyPin(pin)
    }
