package tech.sabitani.core.security.keystore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedKeyStorage
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val preferences: SharedPreferences by lazy { buildPreferences() }

        fun putBytes(
            key: String,
            value: ByteArray,
        ) {
            preferences
                .edit()
                .putString(key, encodeBytes(value))
                .apply()
        }

        fun getBytes(key: String): ByteArray? = preferences.getString(key, null)?.let(::decodeBytes)

        fun contains(key: String): Boolean = preferences.contains(key)

        fun remove(key: String) {
            preferences.edit().remove(key).apply()
        }

        private fun buildPreferences(): SharedPreferences {
            val masterKey =
                MasterKey
                    .Builder(context, MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            return EncryptedSharedPreferences.create(
                context,
                PREFERENCES_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        private companion object {
            const val PREFERENCES_NAME = "sabitani_secure_prefs"
            const val MASTER_KEY_ALIAS = "sabitani_master_key"
        }
    }

private fun encodeBytes(bytes: ByteArray): String = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

private fun decodeBytes(encoded: String): ByteArray = android.util.Base64.decode(encoded, android.util.Base64.NO_WRAP)
