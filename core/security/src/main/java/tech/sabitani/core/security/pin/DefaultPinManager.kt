package tech.sabitani.core.security.pin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tech.sabitani.core.security.crypto.AesGcmCipher
import tech.sabitani.core.security.crypto.EncryptedPayload
import tech.sabitani.core.security.crypto.Pbkdf2KeyDerivation
import tech.sabitani.core.security.database.PinAwareDatabaseKeyHolder
import tech.sabitani.core.security.keystore.EncryptedKeyStorage
import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPinManager
    @Inject
    constructor(
        private val storage: EncryptedKeyStorage,
        private val keyHolder: PinAwareDatabaseKeyHolder,
    ) : PinManager {
        private val mutex = Mutex()

        override fun isPinEnabled(): Boolean = storage.contains(KEY_PIN_SALT) && storage.contains(KEY_DEK_WRAPPED)

        override suspend fun setupPin(pin: CharArray): SetupPinResult =
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    if (isPinEnabled()) {
                        return@withLock SetupPinResult.AlreadyEnabled
                    }
                    val dek = keyHolder.requireUnlockedPassphrase()
                    val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
                    val kek = Pbkdf2KeyDerivation.deriveKey(pin, salt)
                    val wrapped = AesGcmCipher.encrypt(kek, dek)
                    storage.putBytes(KEY_PIN_SALT, salt)
                    storage.putBytes(KEY_DEK_WRAPPED, wrapped.toBytes())
                    storage.remove(KEY_DEK_PLAIN)
                    SetupPinResult.Success
                }
            }

        override suspend fun verifyPin(pin: CharArray): VerifyPinResult =
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    val salt = storage.getBytes(KEY_PIN_SALT) ?: return@withLock VerifyPinResult.PinNotConfigured
                    val wrappedBytes = storage.getBytes(KEY_DEK_WRAPPED) ?: return@withLock VerifyPinResult.PinNotConfigured
                    val kek = Pbkdf2KeyDerivation.deriveKey(pin, salt)
                    val payload = EncryptedPayload.fromBytes(wrappedBytes)
                    try {
                        val dek = AesGcmCipher.decrypt(kek, payload)
                        keyHolder.setUnlockedPassphrase(dek)
                        VerifyPinResult.Success
                    } catch (_: AEADBadTagException) {
                        VerifyPinResult.Incorrect
                    }
                }
            }

        override suspend fun disablePin(currentPin: CharArray): Boolean =
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    val salt = storage.getBytes(KEY_PIN_SALT) ?: return@withLock false
                    val wrappedBytes = storage.getBytes(KEY_DEK_WRAPPED) ?: return@withLock false
                    val kek = Pbkdf2KeyDerivation.deriveKey(currentPin, salt)
                    val payload = EncryptedPayload.fromBytes(wrappedBytes)
                    val dek =
                        try {
                            AesGcmCipher.decrypt(kek, payload)
                        } catch (_: AEADBadTagException) {
                            return@withLock false
                        }
                    storage.putBytes(KEY_DEK_PLAIN, dek)
                    storage.remove(KEY_PIN_SALT)
                    storage.remove(KEY_DEK_WRAPPED)
                    keyHolder.setUnlockedPassphrase(dek)
                    true
                }
            }

        override fun lock() {
            keyHolder.lock()
        }

        private companion object {
            const val KEY_PIN_SALT = "pin_salt_v1"
            const val KEY_DEK_WRAPPED = "db_passphrase_wrapped_v1"
            const val KEY_DEK_PLAIN = "db_passphrase_v1"
            const val SALT_LENGTH_BYTES = 16
        }
    }
