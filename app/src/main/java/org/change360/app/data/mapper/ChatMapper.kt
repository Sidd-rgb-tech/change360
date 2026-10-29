package org.change360.app.data.mapper

import org.change360.app.data.local.entity.ChatMessageEntity
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.model.MessageSender

/**
 * Bi-directional mapper transforming between [ChatMessageEntity] and [ChatMessage].
 */
fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        sender = MessageSender.fromString(sender),
        content = content,
        suggestedActionCategory = suggestedActionCategory?.let { ChallengeCategory.fromString(it) },
        timestampEpochMillis = timestampEpochMillis
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        sender = sender.name,
        content = content,
        suggestedActionCategory = suggestedActionCategory?.name,
        timestampEpochMillis = timestampEpochMillis
    )
}

