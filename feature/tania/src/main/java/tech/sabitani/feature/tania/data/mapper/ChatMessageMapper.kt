package tech.sabitani.feature.tania.data.mapper

import kotlinx.datetime.Instant
import tech.sabitani.core.database.entity.ChatMessageEntity
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.model.ChatRole

internal fun ChatMessageEntity.toDomain(): ChatMessage =
    ChatMessage(
        id = id,
        role = ChatRole.valueOf(role),
        text = text,
        createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
    )

internal fun ChatMessage.toEntity(): ChatMessageEntity =
    ChatMessageEntity(
        id = id,
        role = role.name,
        text = text,
        createdAtEpochMillis = createdAt.toEpochMilliseconds(),
    )
