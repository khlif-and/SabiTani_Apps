package tech.sabitani.core.data.dashboard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.core.data.dashboard.DashboardRepository
import tech.sabitani.core.data.dashboard.DashboardRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DashboardDataModule {
    @Binds
    @Singleton
    abstract fun bindsDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}
