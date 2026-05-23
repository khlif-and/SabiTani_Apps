package tech.sabitani.core.network.gemini.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.sabitani.core.network.BuildConfig
import tech.sabitani.core.network.gemini.GeminiConfig
import tech.sabitani.core.network.gemini.GeminiEndpoints
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GeminiConfigModule {
    @Provides
    @Singleton
    fun providesGeminiConfig(): GeminiConfig =
        GeminiConfig(
            apiKey = BuildConfig.GEMINI_API_KEY,
            baseUrl = GeminiEndpoints.BASE_URL,
            model = BuildConfig.GEMINI_MODEL.ifBlank { GeminiEndpoints.DEFAULT_MODEL },
        )
}
