package org.change360.app.domain.model

/**
 * Originator identity for conversational exchanges in the Eco-AI Coach dialogue.
 */
enum class MessageSender {
    USER,
    AI;

    companion object {
        fun fromString(value: String?): MessageSender {
            if (value.isNullOrBlank()) return USER
            return entries.find { it.name.equals(value.trim(), ignoreCase = true) } ?: USER
        }
    }
}

/**
 * Domain model representing an exchange in the sustainability AI Coach stream.
 *
 * Persists user inquiries, tailored guidance, and linked actionable categories.
 */
data class ChatMessage(
    val id: Long = 0L,
    val sender: MessageSender,
    val content: String,
    val suggestedActionCategory: ChallengeCategory? = null,
    val timestampEpochMillis: Long = System.currentTimeMillis()
)

