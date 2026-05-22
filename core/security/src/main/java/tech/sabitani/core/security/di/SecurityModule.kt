package tech.sabitani.core.security.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.sabitani.core.security.database.DatabaseKeyProvider
import tech.sabitani.core.security.database.DefaultDatabaseKeyProvider
import tech.sabitani.core.security.keystore.EncryptedKeyStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SecurityModule {
    @Provides
    @Singleton
    fun providesEncryptedKeyStorage(
        @ApplicationContext context: Context,
    ): EncryptedKeyStorage = EncryptedKeyStorage(context)

    @Provides
    @Singleton
    fun providesDatabaseKeyProvider(storage: EncryptedKeyStorage): DatabaseKeyProvider = DefaultDatabaseKeyProvider(storage)
}
