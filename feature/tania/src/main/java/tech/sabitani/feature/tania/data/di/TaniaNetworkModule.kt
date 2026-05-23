package tech.sabitani.feature.tania.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import tech.sabitani.core.network.gemini.GeminiConfig
import tech.sabitani.feature.tania.data.remote.GeminiClientFactory
import tech.sabitani.feature.tania.data.remote.api.GeminiApi
import tech.sabitani.feature.tania.data.remote.interceptor.GeminiAuthInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TaniaNetworkModule {
    @Provides
    @Singleton
    fun providesGeminiApi(
        config: GeminiConfig,
        authInterceptor: GeminiAuthInterceptor,
        json: Json,
    ): GeminiApi = GeminiClientFactory.create(config = config, authInterceptor = authInterceptor, json = json)
}
