package tech.sabitani.feature.lock.domain.usecase

import tech.sabitani.core.security.pin.PinManager
import tech.sabitani.core.security.pin.SetupPinResult
import javax.inject.Inject

class SetupPinUseCase
    @Inject
    constructor(
        private val pinManager: PinManager,
    ) {
        suspend operator fun invoke(pin: CharArray): SetupPinResult = pinManager.setupPin(pin)
    }
