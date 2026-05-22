package tech.sabitani.feature.onboarding.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.feature.onboarding.data.repository.OnboardingPreferencesRepositoryImpl
import tech.sabitani.feature.onboarding.domain.repository.OnboardingPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OnboardingDataModule {
    @Binds
    @Singleton
    abstract fun bindsOnboardingPreferencesRepository(impl: OnboardingPreferencesRepositoryImpl): OnboardingPreferencesRepository
}
