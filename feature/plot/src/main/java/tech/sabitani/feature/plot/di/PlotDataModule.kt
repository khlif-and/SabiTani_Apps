package tech.sabitani.feature.plot.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.feature.plot.data.repository.FarmRepositoryImpl
import tech.sabitani.feature.plot.data.repository.PlotRepositoryImpl
import tech.sabitani.feature.plot.domain.repository.FarmRepository
import tech.sabitani.feature.plot.domain.repository.PlotRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PlotDataModule {
    @Binds
    @Singleton
    abstract fun bindsFarmRepository(impl: FarmRepositoryImpl): FarmRepository

    @Binds
    @Singleton
    abstract fun bindsPlotRepository(impl: PlotRepositoryImpl): PlotRepository
}
