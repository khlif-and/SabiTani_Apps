package tech.sabitani.feature.tania.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import tech.sabitani.core.network.gemini.GeminiConfig
import javax.inject.Inject

internal class GeminiAuthInterceptor
    @Inject
    constructor(
        private val config: GeminiConfig,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request =
                chain
                    .request()
                    .newBuilder()
                    .header(HEADER_API_KEY, config.apiKey)
                    .build()
            return chain.proceed(request)
        }

        private companion object {
            const val HEADER_API_KEY = "x-goog-api-key"
        }
    }
