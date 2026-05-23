package tech.sabitani.feature.tania.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.feature.tania.data.repository.ChatRepositoryImpl
import tech.sabitani.feature.tania.domain.repository.ChatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TaniaChatModule {
    @Binds
    @Singleton
    abstract fun bindsChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
