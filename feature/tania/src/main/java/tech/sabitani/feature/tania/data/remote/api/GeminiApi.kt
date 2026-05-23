package tech.sabitani.feature.tania.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import tech.sabitani.feature.tania.data.remote.dto.GenerateContentRequestDto
import tech.sabitani.feature.tania.data.remote.dto.GenerateContentResponseDto

internal interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Body request: GenerateContentRequestDto,
    ): GenerateContentResponseDto
}
