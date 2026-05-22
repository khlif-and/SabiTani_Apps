package tech.sabitani.core.security.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.core.security.biometric.BiometricAuthenticator
import tech.sabitani.core.security.biometric.DefaultBiometricAuthenticator
import tech.sabitani.core.security.database.DatabaseKeyProvider
import tech.sabitani.core.security.database.PinAwareDatabaseKeyHolder
import tech.sabitani.core.security.database.PinAwareDatabaseKeyProvider
import tech.sabitani.core.security.lock.DefaultLockSettingsRepository
import tech.sabitani.core.security.lock.LockSettingsRepository
import tech.sabitani.core.security.pin.DefaultPinManager
import tech.sabitani.core.security.pin.PinManager

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecurityModule {
    @Binds
    abstract fun bindsDatabaseKeyProvider(impl: PinAwareDatabaseKeyProvider): DatabaseKeyProvider

    @Binds
    abstract fun bindsPinAwareDatabaseKeyHolder(impl: PinAwareDatabaseKeyProvider): PinAwareDatabaseKeyHolder

    @Binds
    abstract fun bindsPinManager(impl: DefaultPinManager): PinManager

    @Binds
    abstract fun bindsBiometricAuthenticator(impl: DefaultBiometricAuthenticator): BiometricAuthenticator

    @Binds
    abstract fun bindsLockSettingsRepository(impl: DefaultLockSettingsRepository): LockSettingsRepository
}
