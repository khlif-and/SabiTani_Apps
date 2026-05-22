package tech.sabitani.core.security.database

interface PinAwareDatabaseKeyHolder {
    fun requireUnlockedPassphrase(): ByteArray

    fun setUnlockedPassphrase(passphrase: ByteArray)

    fun lock()
}
