package tech.sabitani.core.security.database

interface DatabaseKeyProvider {
    suspend fun getPassphrase(): ByteArray
}
