package tech.sabitani.core.security.biometric

import androidx.biometric.BiometricPrompt
import tech.sabitani.core.security.database.PinAwareDatabaseKeyHolder
import tech.sabitani.core.security.keystore.EncryptedKeyStorage
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricDekUnwrapper
    @Inject
    constructor(
        private val biometricKeyStore: BiometricKeyStore,
        private val storage: EncryptedKeyStorage,
        private val keyHolder: PinAwareDatabaseKeyHolder,
    ) {
        fun prepareEnrolCipher(): Cipher = biometricKeyStore.encryptCipher()

        fun prepareUnlockCipher(): Cipher? {
            val ivAndCiphertext = storage.getBytes(KEY_BIOMETRIC_WRAPPED_DEK) ?: return null
            if (ivAndCiphertext.size <= IV_LENGTH) return null
            val iv = ivAndCiphertext.copyOfRange(0, IV_LENGTH)
            return biometricKeyStore.decryptCipher(iv)
        }

        fun enrol(cipher: Cipher) {
            val dek = keyHolder.requireUnlockedPassphrase()
            val ciphertext = cipher.doFinal(dek)
            storage.putBytes(KEY_BIOMETRIC_WRAPPED_DEK, cipher.iv + ciphertext)
        }

        fun unlockWithCipher(cipher: Cipher): Boolean {
            val ivAndCiphertext = storage.getBytes(KEY_BIOMETRIC_WRAPPED_DEK) ?: return false
            if (ivAndCiphertext.size <= IV_LENGTH) return false
            val ciphertext = ivAndCiphertext.copyOfRange(IV_LENGTH, ivAndCiphertext.size)
            val dek = cipher.doFinal(ciphertext)
            keyHolder.setUnlockedPassphrase(dek)
            return true
        }

        fun unwrapCryptoObject(result: BiometricPrompt.CryptoObject?): Cipher? = result?.cipher

        fun clear() {
            biometricKeyStore.deleteKey()
            storage.remove(KEY_BIOMETRIC_WRAPPED_DEK)
        }

        private companion object {
            const val KEY_BIOMETRIC_WRAPPED_DEK = "biometric_wrapped_dek_v1"
            const val IV_LENGTH = 12
        }
    }
