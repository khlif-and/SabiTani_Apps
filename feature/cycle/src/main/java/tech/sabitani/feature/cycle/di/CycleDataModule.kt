package tech.sabitani.feature.cycle.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import tech.sabitani.feature.cycle.data.repository.CropCycleRepositoryImpl
import tech.sabitani.feature.cycle.data.repository.FarmActivityRepositoryImpl
import tech.sabitani.feature.cycle.data.repository.TransactionRepositoryImpl
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository
import tech.sabitani.feature.cycle.domain.repository.FarmActivityRepository
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CycleDataModule {

    @Binds
    @Singleton
    abstract fun bindsCropCycleRepository(impl: CropCycleRepositoryImpl): CropCycleRepository

    @Binds
    @Singleton
    abstract fun bindsFarmActivityRepository(
        impl: FarmActivityRepositoryImpl,
    ): FarmActivityRepository

    @Binds
    @Singleton
    abstract fun bindsTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}
