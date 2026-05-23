package tech.sabitani.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import tech.sabitani.core.database.SabiTaniDatabase
import tech.sabitani.core.database.dao.ChatMessageDao
import tech.sabitani.core.database.dao.CropCycleDao
import tech.sabitani.core.database.dao.FarmActivityDao
import tech.sabitani.core.database.dao.FarmDao
import tech.sabitani.core.database.dao.PlotDao
import tech.sabitani.core.database.dao.TransactionDao
import tech.sabitani.core.database.migration.SABITANI_DATABASE_MIGRATIONS
import tech.sabitani.core.security.database.DatabaseKeyProvider
import javax.inject.Singleton

private const val DATABASE_NAME = "sabitani.db"

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesSabiTaniDatabase(
        @ApplicationContext context: Context,
        keyProvider: DatabaseKeyProvider,
    ): SabiTaniDatabase {
        System.loadLibrary("sqlcipher")
        val passphrase = runBlocking { keyProvider.getPassphrase() }
        val factory = SupportOpenHelperFactory(passphrase)
        return Room
            .databaseBuilder(
                context = context,
                klass = SabiTaniDatabase::class.java,
                name = DATABASE_NAME,
            ).openHelperFactory(factory)
            .addMigrations(*SABITANI_DATABASE_MIGRATIONS)
            .build()
    }

    @Provides
    fun providesFarmDao(database: SabiTaniDatabase): FarmDao = database.farmDao()

    @Provides
    fun providesPlotDao(database: SabiTaniDatabase): PlotDao = database.plotDao()

    @Provides
    fun providesCropCycleDao(database: SabiTaniDatabase): CropCycleDao = database.cropCycleDao()

    @Provides
    fun providesFarmActivityDao(database: SabiTaniDatabase): FarmActivityDao = database.farmActivityDao()

    @Provides
    fun providesTransactionDao(database: SabiTaniDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun providesChatMessageDao(database: SabiTaniDatabase): ChatMessageDao = database.chatMessageDao()

    @Provides
    @Singleton
    fun providesClock(): Clock = Clock.System
}
