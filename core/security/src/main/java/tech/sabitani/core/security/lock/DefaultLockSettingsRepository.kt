package tech.sabitani.core.security.lock

import tech.sabitani.core.security.keystore.EncryptedKeyStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLockSettingsRepository
    @Inject
    constructor(
        private val storage: EncryptedKeyStorage,
    ) : LockSettingsRepository {
        override fun isBiometricEnabled(): Boolean = storage.contains(KEY_BIOMETRIC_ENABLED)

        override fun setBiometricEnabled(enabled: Boolean) {
            if (enabled) {
                storage.putBytes(KEY_BIOMETRIC_ENABLED, byteArrayOf(1))
            } else {
                storage.remove(KEY_BIOMETRIC_ENABLED)
            }
        }

        private companion object {
            const val KEY_BIOMETRIC_ENABLED = "biometric_enabled_v1"
        }
    }
