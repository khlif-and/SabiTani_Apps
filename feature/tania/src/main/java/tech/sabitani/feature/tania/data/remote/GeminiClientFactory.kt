package tech.sabitani.feature.tania.data.remote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import tech.sabitani.core.network.gemini.GeminiConfig
import tech.sabitani.feature.tania.data.remote.api.GeminiApi
import tech.sabitani.feature.tania.data.remote.interceptor.GeminiAuthInterceptor
import java.util.concurrent.TimeUnit

internal object GeminiClientFactory {
    private const val TIMEOUT_SECONDS = 60L
    private const val MEDIA_TYPE_JSON = "application/json"

    fun create(
        config: GeminiConfig,
        authInterceptor: GeminiAuthInterceptor,
        json: Json,
    ): GeminiApi {
        val client = buildOkHttpClient(authInterceptor)
        val retrofit = buildRetrofit(client = client, baseUrl = config.baseUrl, json = json)
        return retrofit.create(GeminiApi::class.java)
    }

    private fun buildOkHttpClient(authInterceptor: GeminiAuthInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                },
            ).connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    private fun buildRetrofit(
        client: OkHttpClient,
        baseUrl: String,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(MEDIA_TYPE_JSON.toMediaType()))
            .build()
}
