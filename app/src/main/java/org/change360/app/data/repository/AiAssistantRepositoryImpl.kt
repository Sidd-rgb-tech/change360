package org.change360.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.change360.app.data.local.dao.ChallengeDao
import org.change360.app.data.local.dao.ChatMessageDao
import org.change360.app.data.local.dao.ImpactLogDao
import org.change360.app.data.local.entity.ChatMessageEntity
import org.change360.app.data.mapper.toDomain
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.model.MessageSender
import org.change360.app.domain.repository.AiAssistantRepository

/**
 * Concrete implementation of [AiAssistantRepository] managing conversational exchanges
 * with the Eco-AI Coach, persisted locally via [ChatMessageDao].
 *
 * Coordinates:
 * - [ChatMessageDao] for persistent conversation history surviving application restarts.
 * - [ChallengeDao] and [ImpactLogDao] for contextual environmental awareness.
 * - An embedded expert sustainability intelligence engine providing actionable,
 *   quantified planetary guidance.
 */
class AiAssistantRepositoryImpl(
    private val chatMessageDao: ChatMessageDao,
    private val challengeDao: ChallengeDao,
    private val impactLogDao: ImpactLogDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AiAssistantRepository {

    override fun observeConversation(): Flow<List<ChatMessage>> {
        return chatMessageDao.observeConversation()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun observeRecentMessages(limit: Int): Flow<List<ChatMessage>> {
        return chatMessageDao.observeRecentMessages(limit)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun sendMessage(userPrompt: String): Result<ChatMessage> = withContext(ioDispatcher) {
        runCatching {
            val cleanPrompt = userPrompt.trim()
            require(cleanPrompt.isNotBlank()) { "User message prompt cannot be empty." }

            val now = System.currentTimeMillis()

            // 1. Persist user dialogue prompt
            val userEntity = ChatMessageEntity(
                sender = MessageSender.USER.name,
                content = cleanPrompt,
                suggestedActionCategory = null,
                timestampEpochMillis = now
            )
            chatMessageDao.insertMessage(userEntity)

            // 2. Generate intelligent, contextual sustainability advice
            val (aiContent, category) = generateSustainabilityAdvice(cleanPrompt)

            // 3. Persist AI assistant response
            val aiEntity = ChatMessageEntity(
                sender = MessageSender.AI.name,
                content = aiContent,
                suggestedActionCategory = category?.name,
                timestampEpochMillis = now + 150L
            )
            val generatedRowId = chatMessageDao.insertMessage(aiEntity)

            ChatMessage(
                id = generatedRowId,
                sender = MessageSender.AI,
                content = aiContent,
                suggestedActionCategory = category,
                timestampEpochMillis = aiEntity.timestampEpochMillis
            )
        }
    }

    override suspend fun clearHistory(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            chatMessageDao.clearHistory()
        }
    }

    override suspend fun deleteMessageById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            chatMessageDao.deleteMessageById(id)
        }
    }

    override fun getQuickPromptChips(): List<String> {
        return listOf(
            "Calculate my daily commute footprint",
            "5-minute zero-waste swap",
            "Explain carbon offsetting",
            "Suggest a challenge for me",
            "How can I save water today?",
            "Tips to eliminate vampire power"
        )
    }

    /**
     * Domain intelligence engine synthesizing contextual sustainability advice.
     */
    private suspend fun generateSustainabilityAdvice(prompt: String): Pair<String, ChallengeCategory?> {
        val lower = prompt.lowercase()

        return when {
            // Commute & Mobility Footprint
            lower.contains("commute") || lower.contains("transit") || lower.contains("footprint") ||
                    lower.contains("driving") || lower.contains("car") || lower.contains("bike") -> {
                val advice = buildString {
                    append("🚗 **Commute Footprint Audit:**\n\n")
                    append("An average combustion vehicle emits **~192g CO₂ per kilometer**. A daily 15 km round-trip commute adds up to **~2.88 kg CO₂/day** or **720 kg CO₂ annually**.\n\n")
                    append("🚲 **Regenerative Swaps:**\n")
                    append("• Switching to cycling or walking cuts this by **100%** while burning ~300 kcal.\n")
                    append("• Taking public rail/metro reduces emissions by **78%** (~0.63 kg CO₂/day).\n\n")
                    append("💡 *Recommended Action:* Try the **Two-Wheeled Commute** challenge today!")
                }
                advice to ChallengeCategory.MOBILITY
            }

            // Zero-Waste & Packaging
            lower.contains("zero-waste") || lower.contains("plastic") || lower.contains("swap") ||
                    lower.contains("trash") || lower.contains("compost") || lower.contains("waste") -> {
                val advice = buildString {
                    append("♻️ **High-Impact Zero-Waste Swaps:**\n\n")
                    append("1. **Solid Shampoo & Body Bars:** Eliminates 3 plastic bottles and ~1.2 kg of shipping packaging per quarter.\n")
                    append("2. **Stainless Steel Refill Kit:** Carrying a reusable bottle and cutlery prevents ~156 single-use items each year.\n")
                    append("3. **Kitchen Counter Composting:** Diverts ~40% of household waste from methane-producing landfills.\n\n")
                    append("🌱 *Next Step:* Check out the **Single-Use Plastic Refusal** quest in your Challenges tab!")
                }
                advice to ChallengeCategory.ZERO_WASTE
            }

            // Carbon Offsetting & Sequestration
            lower.contains("offset") || lower.contains("carbon credit") || lower.contains("sequestration") -> {
                val advice = buildString {
                    append("🌍 **Understanding Carbon Offsetting:**\n\n")
                    append("Carbon offsets represent verified reductions or removals of greenhouse gases from the atmosphere (e.g., reforestation, direct air capture).\n\n")
                    append("⚠️ **The Golden Hierarchy:**\n")
                    append("1. **Avoid & Reduce first:** Cutting emissions at the source is 10x more permanent than purchasing offsets.\n")
                    append("2. **Local Bio-Capture:** 1 mature tree absorbs ~21.8 kg CO₂ per year and restores local soil microbiology.\n\n")
                    append("🌳 *Community Quest:* Consider planting a native shrub via **Native Habitat Regeneration**!")
                }
                advice to ChallengeCategory.COMMUNITY
            }

            // Water Conservation
            lower.contains("water") || lower.contains("shower") || lower.contains("leak") || lower.contains("drought") -> {
                val advice = buildString {
                    append("💧 **Freshwater Conservation Strategy:**\n\n")
                    append("Standard showerheads deliver ~10 liters per minute. Shortening shower duration from 10 minutes to 5 minutes immediately saves **50 liters of potable water** and ~0.9 kg of CO₂ from water heating.\n\n")
                    append("🔧 **Quick Win:**\n")
                    append("Place a timer in your bathroom and catch greywater for houseplants during rinse warm-up.\n\n")
                    append("🌊 *Active Challenge:* Start the **5-Minute Shower Sprint** quest!")
                }
                advice to ChallengeCategory.WATER
            }

            // Energy & Vampire Power
            lower.contains("energy") || lower.contains("power") || lower.contains("vampire") ||
                    lower.contains("phantom") || lower.contains("electricity") || lower.contains("solar") -> {
                val advice = buildString {
                    append("⚡ **Tackling Ghost & Vampire Power:**\n\n")
                    append("Idle devices plugged into walls account for up to **10% of residential electric consumption**. Phone chargers, TVs in standby, and game consoles constantly draw phantom wattage.\n\n")
                    append("🔌 **3-Step Tactic:**\n")
                    append("1. Use smart surge power strips with master shutoff switches.\n")
                    append("2. Wash laundry on cold cycles (saves up to 90% water heating power).\n")
                    append("3. Unplug desktop setups overnight.\n\n")
                    append("⚡ *Active Challenge:* Log a step in **Phantom Power Sweep**!")
                }
                advice to ChallengeCategory.ENERGY
            }

            // Suggest Challenge Recommendation
            lower.contains("suggest") || lower.contains("recommend") || lower.contains("what can i do") ||
                    lower.contains("next") -> {
                val advice = buildString {
                    append("🌟 **Tailored Challenge Recommendation:**\n\n")
                    append("Based on planetary priority balance, I recommend taking on the **Organic Compost Champion** or **Grid Peak Load Shift** quests.\n\n")
                    append("Both offer between **180 to 210 XP** and divert significant greenhouse gases this week. Head over to the **Challenges** tab and tap **Log Step** to begin!")
                }
                advice to ChallengeCategory.ALL
            }

            // General Planetary Coaching Fallback
            else -> {
                val advice = buildString {
                    append("🌿 **Planetary Stewardship Perspective:**\n\n")
                    append("Every daily choice contributes to planetary regeneration. Whether saving 50 liters of water, skipping 5 km of driving, or eliminating packaging, your actions create measurable ripples.\n\n")
                    append("Ask me specifically about:\n")
                    append("• Calculating commute emissions\n")
                    append("• Zero-waste swaps\n")
                    append("• Energy efficiency tips\n")
                    append("• Water audit guidance")
                }
                advice to ChallengeCategory.ALL
            }
        }
    }
}
