package tech.sabitani.core.security.lock

interface LockSettingsRepository {
    fun isBiometricEnabled(): Boolean

    fun setBiometricEnabled(enabled: Boolean)
}
