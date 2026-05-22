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
class PinAwareDatabaseKeyProvider
    @Inject
    constructor(
        private val storage: EncryptedKeyStorage,
    ) : DatabaseKeyProvider,
        PinAwareDatabaseKeyHolder {
        private val mutex = Mutex()

        @Volatile
        private var unlockedPassphrase: ByteArray? = null

        override suspend fun getPassphrase(): ByteArray =
            withContext(Dispatchers.IO) {
                mutex.withLock { resolvePassphrase() }
            }

        override fun requireUnlockedPassphrase(): ByteArray {
            unlockedPassphrase?.let { return it }
            val plain =
                storage.getBytes(KEY_DEK_PLAIN)
                    ?: error("Database passphrase is locked; unlock with PIN first")
            unlockedPassphrase = plain
            return plain
        }

        override fun setUnlockedPassphrase(passphrase: ByteArray) {
            unlockedPassphrase = passphrase
        }

        override fun lock() {
            unlockedPassphrase = null
        }

        private fun resolvePassphrase(): ByteArray {
            unlockedPassphrase?.let { return it }
            val plain = storage.getBytes(KEY_DEK_PLAIN)
            if (plain != null) {
                unlockedPassphrase = plain
                return plain
            }
            val isWrapped = storage.contains(KEY_DEK_WRAPPED)
            if (isWrapped) {
                error("Database passphrase is locked; unlock with PIN first")
            }
            val generated = ByteArray(PASSPHRASE_BYTE_LENGTH).also { SecureRandom().nextBytes(it) }
            storage.putBytes(KEY_DEK_PLAIN, generated)
            unlockedPassphrase = generated
            return generated
        }

        private companion object {
            const val KEY_DEK_PLAIN = "db_passphrase_v1"
            const val KEY_DEK_WRAPPED = "db_passphrase_wrapped_v1"
            const val PASSPHRASE_BYTE_LENGTH = 32
        }
    }
