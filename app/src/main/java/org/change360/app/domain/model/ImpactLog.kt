package org.change360.app.domain.model

/**
 * Domain model representing an immutable verified ledger entry of planetary impact.
 *
 * Captures atomic environmental savings: CO2 avoided, freshwater preserved,
 * and solid landfill waste diverted.
 */
data class ImpactLog(
    val id: Long = 0L,
    val userId: String,
    val sourceChallengeId: String? = null,
    val actionTitle: String,
    val co2SavedKg: Float,
    val waterSavedLiters: Float,
    val wasteSavedKg: Float,
    val loggedAtEpochMillis: Long = System.currentTimeMillis()
)

