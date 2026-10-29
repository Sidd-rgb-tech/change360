package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.model.User
import org.change360.app.domain.repository.AuthRepository
import org.change360.app.domain.repository.ChallengeRepository
import org.change360.app.domain.repository.ImpactRepository
import java.util.Calendar

/**
 * Curated planetary stewardship tip presented on the Home dashboard.
 */
data class DailyEcoTip(
    val id: String,
    val title: String,
    val description: String,
    val category: ChallengeCategory
)

/**
 * Aggregated domain state model for the primary Home dashboard.
 *
 * Consolidates user profile, active challenges carousel, today's environmental metrics,
 * completion counts, and the daily planetary action tip.
 */
data class DashboardOverview(
    val user: User?,
    val activeChallenges: List<Challenge>,
    val todayImpact: ImpactSummary,
    val completedChallengesCount: Int,
    val totalChallengesCount: Int,
    val dailyTip: DailyEcoTip
)

/**
 * Core Use Case responsible for aggregating disparate streams into a unified Home dashboard overview.
 *
 * Injects:
 * - [AuthRepository] for the active user's Aura level, streak, and XP.
 * - [ChallengeRepository] for active quests and overall completion stats.
 * - [ImpactRepository] for real-time impact logs recorded today.
 */
class GetDashboardOverviewUseCase(
    private val authRepository: AuthRepository,
    private val challengeRepository: ChallengeRepository,
    private val impactRepository: ImpactRepository
) {

    /**
     * Observes real-time dashboard state updates.
     *
     * @param maxActiveChallenges Maximum number of active quests exposed for the carousel (defaults to 3).
     */
    operator fun invoke(maxActiveChallenges: Int = 3): Flow<DashboardOverview> {
        val startOfToday = getStartOfDayEpochMillis()

        return combine(
            authRepository.observeActiveUser(),
            challengeRepository.observeActiveChallenges(),
            impactRepository.observeLogsSince(startOfToday),
            challengeRepository.observeCompletedCount(),
            challengeRepository.observeTotalCount()
        ) { user, activeChallenges, todayLogs, completedCount, totalCount ->
            DashboardOverview(
                user = user,
                activeChallenges = activeChallenges.take(maxActiveChallenges),
                todayImpact = ImpactSummary.calculate(todayLogs, TimeRange.WEEK),
                completedChallengesCount = completedCount,
                totalChallengesCount = totalCount,
                dailyTip = resolveDailyTip()
            )
        }
    }

    /**
     * Synchronously retrieves a single-shot snapshot of the dashboard overview.
     */
    suspend fun getSnapshot(maxActiveChallenges: Int = 3): DashboardOverview {
        return invoke(maxActiveChallenges).first()
    }

    /**
     * Resolves an inspiring planetary stewardship tip based on the current day of the year.
     */
    private fun resolveDailyTip(): DailyEcoTip {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear % CURATED_TIPS.size).coerceAtLeast(0)
        return CURATED_TIPS[index]
    }

    /**
     * Computes the epoch timestamp representing 00:00:00.000 of the current day.
     */
    private fun getStartOfDayEpochMillis(now: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    companion object {
        /**
         * Curated rotation of actionable planetary micro-actions.
         */
        val CURATED_TIPS: List<DailyEcoTip> = listOf(
            DailyEcoTip(
                id = "tip_vampire_power",
                title = "Vampire Power Audit",
                description = "Unplugging idle phone chargers and kitchen electronics can trim up to 10% from your home electricity demand.",
                category = ChallengeCategory.ENERGY
            ),
            DailyEcoTip(
                id = "tip_cold_wash",
                title = "Cold Water Wash",
                description = "Washing laundry at 30°C instead of 60°C uses ~60% less energy and protects clothing fibers from premature wear.",
                category = ChallengeCategory.ENERGY
            ),
            DailyEcoTip(
                id = "tip_shower_sprint",
                title = "Shower Sprint",
                description = "Shortening daily showers by 3 minutes saves over 30 liters of potable water and cuts water-heating gas/power.",
                category = ChallengeCategory.WATER
            ),
            DailyEcoTip(
                id = "tip_micro_leak",
                title = "Silent Leak Hunt",
                description = "A dripping tap at 1 drop per second wastes 11,000+ liters of clean freshwater each year. Check your seals today.",
                category = ChallengeCategory.WATER
            ),
            DailyEcoTip(
                id = "tip_package_free",
                title = "Package-Free Choices",
                description = "Choosing loose bulk produce over shrink-wrapped vegetables immediately stops non-recyclable soft plastic from landfills.",
                category = ChallengeCategory.ZERO_WASTE
            ),
            DailyEcoTip(
                id = "tip_refill_bottle",
                title = "Hydrate with Stainless Steel",
                description = "Carrying a refillable bottle prevents approximately 156 single-use plastic bottles per practitioner each year.",
                category = ChallengeCategory.ZERO_WASTE
            ),
            DailyEcoTip(
                id = "tip_bike_commute",
                title = "Two-Wheeled Transit",
                description = "Replacing a 5 km car drive with a bicycle cuts ~1 kg of CO2 and eliminates cold-engine urban tailpipe particulate.",
                category = ChallengeCategory.MOBILITY
            ),
            DailyEcoTip(
                id = "tip_train_metro",
                title = "Ride the Rails",
                description = "Electric metro and commuter trains produce up to 80% fewer carbon emissions per passenger kilometer than solo driving.",
                category = ChallengeCategory.MOBILITY
            ),
            DailyEcoTip(
                id = "tip_advocate_poster",
                title = "Amplify the Movement",
                description = "Creating and sharing a CHANGE360 advocacy poster with friends normalizes climate action in your social circle.",
                category = ChallengeCategory.COMMUNITY
            )
        )
    }
}

