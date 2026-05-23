package tech.sabitani.feature.tania.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.feature.tania.data.repository.KnowledgeRepositoryImpl
import tech.sabitani.feature.tania.domain.repository.KnowledgeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TaniaKnowledgeModule {
    @Binds
    @Singleton
    abstract fun bindsKnowledgeRepository(impl: KnowledgeRepositoryImpl): KnowledgeRepository
}
