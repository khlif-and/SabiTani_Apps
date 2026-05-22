package tech.sabitani.core.security.pin

interface PinManager {
    fun isPinEnabled(): Boolean

    suspend fun setupPin(pin: CharArray): SetupPinResult

    suspend fun verifyPin(pin: CharArray): VerifyPinResult

    suspend fun disablePin(currentPin: CharArray): Boolean

    fun lock()
}

sealed interface SetupPinResult {
    data object Success : SetupPinResult

    data object AlreadyEnabled : SetupPinResult
}

sealed interface VerifyPinResult {
    data object Success : VerifyPinResult

    data object Incorrect : VerifyPinResult

    data object PinNotConfigured : VerifyPinResult
}
