package tech.sabitani.core.security.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tech.sabitani.core.security.keystore.EncryptedKeyStorage
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDatabaseKeyProvider
    @Inject
    constructor(
        private val storage: EncryptedKeyStorage,
    ) : DatabaseKeyProvider {
        private val mutex = Mutex()

        override suspend fun getPassphrase(): ByteArray =
            withContext(Dispatchers.IO) {
                mutex.withLock {
                    storage.getBytes(KEY_DATABASE_PASSPHRASE) ?: generateAndStorePassphrase()
                }
            }

        private fun generateAndStorePassphrase(): ByteArray {
            val passphrase = ByteArray(PASSPHRASE_BYTE_LENGTH)
            SecureRandom().nextBytes(passphrase)
            storage.putBytes(KEY_DATABASE_PASSPHRASE, passphrase)
            return passphrase
        }

        private companion object {
            const val KEY_DATABASE_PASSPHRASE = "db_passphrase_v1"
            const val PASSPHRASE_BYTE_LENGTH = 32
        }
    }
