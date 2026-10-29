package org.change360.app.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.change360.app.data.local.entity.ChallengeEntity
import org.change360.app.data.local.entity.ChatMessageEntity
import org.change360.app.data.local.entity.PosterEntity

/**
 * Room database lifecycle callback that pre-populates initial curated challenges,
 * foundational AI assistant welcome dialogue, and starter advocacy poster templates
 * upon first database creation.
 */
class SeedDatabaseCallback(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val databaseProvider: (() -> Change360Database)? = null
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedSynchronously(db)

        databaseProvider?.let { provider ->
            coroutineScope.launch {
                runCatching {
                    val database = provider()
                    seedDatabase(database)
                }
            }
        }
    }

    private fun seedSynchronously(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            // Seed Curated Challenges
            for (challenge in SEED_CHALLENGES) {
                val values = ContentValues().apply {
                    put("id", challenge.id)
                    put("title", challenge.title)
                    put("description", challenge.description)
                    put("category", challenge.category)
                    put("difficulty", challenge.difficulty)
                    put("xpReward", challenge.xpReward)
                    put("co2SavedKg", challenge.co2SavedKg)
                    put("waterSavedLiters", challenge.waterSavedLiters)
                    put("wasteSavedKg", challenge.wasteSavedKg)
                    put("totalSteps", challenge.totalSteps)
                    put("completedSteps", challenge.completedSteps)
                    put("isCompleted", if (challenge.isCompleted) 1 else 0)
                    put("completedAtEpochMillis", challenge.completedAtEpochMillis)
                }
                db.insert("challenges", SQLiteDatabase.CONFLICT_REPLACE, values)
            }

            // Seed Initial Chat Welcome Message
            for (chatMessage in SEED_CHAT_MESSAGES) {
                val values = ContentValues().apply {
                    put("sender", chatMessage.sender)
                    put("content", chatMessage.content)
                    put("suggestedActionCategory", chatMessage.suggestedActionCategory)
                    put("timestampEpochMillis", chatMessage.timestampEpochMillis)
                }
                db.insert("chat_messages", SQLiteDatabase.CONFLICT_REPLACE, values)
            }

            // Seed Curated Poster Templates
            for (poster in SEED_POSTER_TEMPLATES) {
                val values = ContentValues().apply {
                    put("title", poster.title)
                    put("pledgeQuote", poster.pledgeQuote)
                    put("templateStyle", poster.templateStyle)
                    put("accentColorHex", poster.accentColorHex)
                    put("exportedUriString", poster.exportedUriString)
                    put("createdAtEpochMillis", poster.createdAtEpochMillis)
                }
                db.insert("posters", SQLiteDatabase.CONFLICT_REPLACE, values)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {

        /**
         * Programmatically seeds the database using Room DAOs.
         * Ideal for automated tests or manual database resets.
         */
        suspend fun seedDatabase(database: Change360Database) {
            database.challengeDao().insertChallenges(SEED_CHALLENGES)
            database.chatMessageDao().insertMessages(SEED_CHAT_MESSAGES)
            database.posterDao().insertPosters(SEED_POSTER_TEMPLATES)
        }

        val SEED_CHALLENGES: List<ChallengeEntity> = listOf(
            // ENERGY CATEGORY
            ChallengeEntity(
                id = "energy_phantom_power",
                title = "Phantom Power Sweep",
                description = "Unplug all idle electronics, phone chargers, and desktop peripherals overnight to eliminate vampire energy draw.",
                category = "ENERGY",
                difficulty = "EASY",
                xpReward = 100,
                co2SavedKg = 1.2f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 3,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "energy_cold_wash",
                title = "Cold-Wash Revolution",
                description = "Wash all weekly laundry loads using cold cycle (30°C or lower) to slash electrical heating consumption by up to 90%.",
                category = "ENERGY",
                difficulty = "EASY",
                xpReward = 120,
                co2SavedKg = 2.4f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 4,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "energy_peak_shift",
                title = "Grid Peak Load Shift",
                description = "Shift heavy appliance usage (dishwashers, ovens, dryers) outside local peak demand hours to maximize renewable grid mix.",
                category = "ENERGY",
                difficulty = "MEDIUM",
                xpReward = 180,
                co2SavedKg = 3.8f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 5,
                completedSteps = 0,
                isCompleted = false
            ),

            // WATER CATEGORY
            ChallengeEntity(
                id = "water_shower_sprint",
                title = "5-Minute Shower Sprint",
                description = "Reduce everyday shower duration to 5 minutes or less using an ambient timer to prevent clean water depletion.",
                category = "WATER",
                difficulty = "EASY",
                xpReward = 110,
                co2SavedKg = 0.9f,
                waterSavedLiters = 50.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 5,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "water_greywater_reuse",
                title = "Greywater Plant Sanctuary",
                description = "Collect clean rinse water from kitchen cooking and produce washing to irrigate houseplants and garden soil.",
                category = "WATER",
                difficulty = "MEDIUM",
                xpReward = 160,
                co2SavedKg = 0.4f,
                waterSavedLiters = 75.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 4,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "water_leak_detective",
                title = "Home Plumbing Micro-Audit",
                description = "Inspect every tap aerator, toilet flush valve, and pipe joint across your home, repairing or flagging all micro-leaks.",
                category = "WATER",
                difficulty = "HARD",
                xpReward = 230,
                co2SavedKg = 1.6f,
                waterSavedLiters = 140.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 3,
                completedSteps = 0,
                isCompleted = false
            ),

            // ZERO_WASTE CATEGORY
            ChallengeEntity(
                id = "waste_reusable_kit",
                title = "Single-Use Plastic Refusal",
                description = "Equip and carry a reusable tote, stainless water tumbler, and cotton mesh produce bags for all shopping journeys.",
                category = "ZERO_WASTE",
                difficulty = "EASY",
                xpReward = 140,
                co2SavedKg = 1.8f,
                waterSavedLiters = 15.0f,
                wasteSavedKg = 1.5f,
                totalSteps = 7,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "waste_kitchen_compost",
                title = "Organic Compost Champion",
                description = "Divert 100% of organic vegetable scraps, coffee grounds, and fruit rinds away from landfills into compost decomposition.",
                category = "ZERO_WASTE",
                difficulty = "MEDIUM",
                xpReward = 210,
                co2SavedKg = 4.5f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 4.0f,
                totalSteps = 7,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "waste_bulk_pantry",
                title = "Zero-Packaging Pantry",
                description = "Restock dry pantry staples (rice, lentils, oats, seeds) using your own reusable jars at bulk refiller stations.",
                category = "ZERO_WASTE",
                difficulty = "HARD",
                xpReward = 260,
                co2SavedKg = 3.2f,
                waterSavedLiters = 25.0f,
                wasteSavedKg = 3.0f,
                totalSteps = 4,
                completedSteps = 0,
                isCompleted = false
            ),

            // MOBILITY CATEGORY
            ChallengeEntity(
                id = "mobility_active_transit",
                title = "Two-Wheeled Commute",
                description = "Replace personal motor vehicle trips under 5 kilometers with cycling, walking, or non-motorized active transit.",
                category = "MOBILITY",
                difficulty = "MEDIUM",
                xpReward = 190,
                co2SavedKg = 6.2f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 5,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "mobility_public_transit",
                title = "Metro & Rail Transit",
                description = "Choose trains, buses, or electric trams for daily urban transit instead of single-occupancy driving.",
                category = "MOBILITY",
                difficulty = "EASY",
                xpReward = 150,
                co2SavedKg = 5.0f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 4,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "mobility_car_free_weekend",
                title = "Car-Free 48-Hour Weekend",
                description = "Keep your personal vehicle parked for an entire weekend, utilizing public transit, cycling, and walking exclusively.",
                category = "MOBILITY",
                difficulty = "HARD",
                xpReward = 310,
                co2SavedKg = 12.5f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 2,
                completedSteps = 0,
                isCompleted = false
            ),

            // COMMUNITY CATEGORY
            ChallengeEntity(
                id = "community_advocacy_poster",
                title = "Advocacy Poster Broadcast",
                description = "Create an inspirational sustainability poster using the CHANGE360 studio and share it with your social circles.",
                category = "COMMUNITY",
                difficulty = "EASY",
                xpReward = 160,
                co2SavedKg = 2.0f,
                waterSavedLiters = 0.0f,
                wasteSavedKg = 0.5f,
                totalSteps = 1,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "community_neighborhood_cleanup",
                title = "Public Trail & Park Stewardship",
                description = "Dedicate 45 minutes to clearing litter and recyclable debris from a neighborhood park, trail, or waterway.",
                category = "COMMUNITY",
                difficulty = "MEDIUM",
                xpReward = 240,
                co2SavedKg = 1.5f,
                waterSavedLiters = 10.0f,
                wasteSavedKg = 5.0f,
                totalSteps = 3,
                completedSteps = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                id = "community_native_sapling",
                title = "Native Habitat Regeneration",
                description = "Plant a native pollinator-friendly perennial shrub or tree sapling to foster urban biodiversity and carbon capture.",
                category = "COMMUNITY",
                difficulty = "HARD",
                xpReward = 360,
                co2SavedKg = 22.0f,
                waterSavedLiters = 30.0f,
                wasteSavedKg = 0.0f,
                totalSteps = 1,
                completedSteps = 0,
                isCompleted = false
            )
        )

        val SEED_CHAT_MESSAGES: List<ChatMessageEntity> = listOf(
            ChatMessageEntity(
                id = 1L,
                sender = "AI",
                content = "Welcome to CHANGE360! I am your personal Eco-AI Coach. Together, we transform everyday climate awareness into measurable, regenerative planetary impact. Ask me for customized footprint audits, zero-waste swaps, or challenge coaching.",
                suggestedActionCategory = "COMMUNITY",
                timestampEpochMillis = System.currentTimeMillis()
            )
        )

        val SEED_POSTER_TEMPLATES: List<PosterEntity> = listOf(
            PosterEntity(
                id = 1L,
                title = "Bio-Luminescent Pulse",
                pledgeQuote = "Every daily action ripples across our biosphere. Choose regeneration.",
                templateStyle = "BIO_EMERALD",
                accentColorHex = "#00E676",
                exportedUriString = null,
                createdAtEpochMillis = System.currentTimeMillis()
            ),
            PosterEntity(
                id = 2L,
                title = "Oceanic Vitality",
                pledgeQuote = "Protecting living waters begins at home. Small swaps create tidal waves.",
                templateStyle = "OCEANIC_PULSE",
                accentColorHex = "#00BFA5",
                exportedUriString = null,
                createdAtEpochMillis = System.currentTimeMillis()
            ),
            PosterEntity(
                id = 3L,
                title = "Solar Clean Energy",
                pledgeQuote = "Harnessing the power of renewable choices for an electrified, circular tomorrow.",
                templateStyle = "SOLAR_FLARE",
                accentColorHex = "#FFD740",
                exportedUriString = null,
                createdAtEpochMillis = System.currentTimeMillis()
            ),
            PosterEntity(
                id = 4L,
                title = "Midnight Gaia Stewardship",
                pledgeQuote = "In harmony with Earth: 360 degrees of mindful, climate-positive living.",
                templateStyle = "MIDNIGHT_GAIA",
                accentColorHex = "#00E5FF",
                exportedUriString = null,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}

