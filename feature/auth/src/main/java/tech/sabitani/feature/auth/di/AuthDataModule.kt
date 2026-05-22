package tech.sabitani.feature.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.feature.auth.data.repository.FakeAuthRepository
import tech.sabitani.feature.auth.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthDataModule {
    @Binds
    @Singleton
    abstract fun bindsAuthRepository(impl: FakeAuthRepository): AuthRepository
}
