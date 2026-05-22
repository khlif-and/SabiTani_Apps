package tech.sabitani.core.security.biometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricKeyStore
    @Inject
    constructor() {
        fun ensureKeyExists(): SecretKey {
            val keystore =
                java.security.KeyStore
                    .getInstance(ANDROID_KEYSTORE)
                    .apply { load(null) }
            (keystore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
            return generateKey()
        }

        fun encryptCipher(): Cipher {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, ensureKeyExists())
            return cipher
        }

        fun decryptCipher(iv: ByteArray): Cipher {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, ensureKeyExists(), GCMParameterSpec(TAG_LENGTH_BITS, iv))
            return cipher
        }

        fun deleteKey() {
            val keystore =
                java.security.KeyStore
                    .getInstance(ANDROID_KEYSTORE)
                    .apply { load(null) }
            if (keystore.containsAlias(KEY_ALIAS)) {
                keystore.deleteEntry(KEY_ALIAS)
            }
        }

        private fun generateKey(): SecretKey {
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val spec =
                KeyGenParameterSpec
                    .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            generator.init(spec)
            return generator.generateKey()
        }

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "sabitani_biometric_dek_v1"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val TAG_LENGTH_BITS = 128
        }
    }
