package tech.sabitani.core.security.crypto

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal object Pbkdf2KeyDerivation {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 200_000
    private const val KEY_LENGTH_BITS = 256

    fun deriveKey(
        secret: CharArray,
        salt: ByteArray,
    ): ByteArray {
        val spec = PBEKeySpec(secret, salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
