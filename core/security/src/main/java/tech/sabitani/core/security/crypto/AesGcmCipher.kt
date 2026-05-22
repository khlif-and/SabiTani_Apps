package tech.sabitani.core.security.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AesGcmCipher {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH_BYTES = 12
    private const val TAG_LENGTH_BITS = 128

    fun encrypt(
        key: ByteArray,
        plaintext: ByteArray,
    ): EncryptedPayload {
        val iv = ByteArray(IV_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key.toSecretKey(), GCMParameterSpec(TAG_LENGTH_BITS, iv))
        val ciphertext = cipher.doFinal(plaintext)
        return EncryptedPayload(iv = iv, ciphertext = ciphertext)
    }

    fun decrypt(
        key: ByteArray,
        payload: EncryptedPayload,
    ): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key.toSecretKey(), GCMParameterSpec(TAG_LENGTH_BITS, payload.iv))
        return cipher.doFinal(payload.ciphertext)
    }

    private fun ByteArray.toSecretKey(): SecretKey = SecretKeySpec(this, ALGORITHM)
}

internal data class EncryptedPayload(
    val iv: ByteArray,
    val ciphertext: ByteArray,
) {
    fun toBytes(): ByteArray = iv + ciphertext

    companion object {
        private const val IV_LENGTH_BYTES = 12

        fun fromBytes(bytes: ByteArray): EncryptedPayload {
            require(bytes.size > IV_LENGTH_BYTES) { "Encrypted payload too short" }
            val iv = bytes.copyOfRange(0, IV_LENGTH_BYTES)
            val ciphertext = bytes.copyOfRange(IV_LENGTH_BYTES, bytes.size)
            return EncryptedPayload(iv = iv, ciphertext = ciphertext)
        }
    }
}
