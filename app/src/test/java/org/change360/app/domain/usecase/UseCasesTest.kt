package org.change360.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.change360.app.data.local.datastore.UserPreferencesData
import org.change360.app.domain.model.CategoryImpactBreakdown
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeDifficulty
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
import org.change360.app.domain.model.MessageSender
import org.change360.app.domain.model.PosterAuraStyle
import org.change360.app.domain.model.PosterTemplate
import org.change360.app.domain.model.SavedPoster
import org.change360.app.domain.model.TimeRange
import org.change360.app.domain.model.User
import org.change360.app.domain.repository.AiAssistantRepository
import org.change360.app.domain.repository.AuthRepository
import org.change360.app.domain.repository.ChallengeRepository
import org.change360.app.domain.repository.ImpactRepository
import org.change360.app.domain.repository.PosterRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite verifying core Use Cases behavior across authentication,
 * quest completion, dashboard aggregation, impact computation, AI dialogue,
 * and poster generation.
 */
class UseCasesTest {

    // region 1. ObserveAuthStateUseCase Tests
    @Test
    fun observeAuthStateUseCase_emitsAuthenticated_whenSessionIsValid() = runTest {
        val fakeUser = User(
            id = "user_1",
            displayName = "Ramesh",
            email = "ramesh@change360.org",
            ecoPledge = "Cut single-use plastics",
            auraLevel = 2,
            xpPoints = 650,
            currentStreakDays = 5
        )

        val fakeAuthRepo = FakeAuthRepository(
            initialUser = fakeUser,
            initialIsAuth = true
        )

        val useCase = ObserveAuthStateUseCase(fakeAuthRepo)
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val emissions = mutableListOf<AuthState>()

        val job = backgroundScope.launch(testDispatcher) {
            useCase().toList(emissions)
        }

        assertTrue(emissions.contains(AuthState.Loading))
        val authenticatedState = emissions.filterIsInstance<AuthState.Authenticated>().firstOrNull()
        assertNotNull(authenticatedState)
        assertEquals("Ramesh", authenticatedState?.user?.displayName)
        assertEquals(2, authenticatedState?.user?.auraLevel)

        job.cancel()
    }

    @Test
    fun observeAuthStateUseCase_emitsUnauthenticated_whenUserIsNull() = runTest {
        val fakeAuthRepo = FakeAuthRepository(
            initialUser = null,
            initialIsAuth = false
        )

        val useCase = ObserveAuthStateUseCase(fakeAuthRepo)
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val emissions = mutableListOf<AuthState>()

        val job = backgroundScope.launch(testDispatcher) {
            useCase().toList(emissions)
        }

        assertTrue(emissions.contains(AuthState.Unauthenticated))
        job.cancel()
    }
    // endregion

    // region 2. AuthenticateUserUseCase Tests
    @Test
    fun authenticateUserUseCase_signIn_validatesInput() = runTest {
        val fakeAuthRepo = FakeAuthRepository()
        val useCase = AuthenticateUserUseCase(fakeAuthRepo)

        val blankResult = useCase.signIn("   ")
        assertTrue(blankResult.isFailure)

        val invalidEmailResult = useCase.signIn("notanemail")
        assertTrue(invalidEmailResult.isFailure)

        val successResult = useCase.signIn("practitioner@gaia.eco")
        assertTrue(successResult.isSuccess)
        assertEquals("practitioner@gaia.eco", successResult.getOrNull()?.email)
    }

    @Test
    fun authenticateUserUseCase_signUp_createsUser() = runTest {
        val fakeAuthRepo = FakeAuthRepository()
        val useCase = AuthenticateUserUseCase(fakeAuthRepo)

        val result = useCase.signUp(
            displayName = "Maya Angelou",
            email = "maya@planetary.eco",
            ecoPledge = "Zero-waste pantry & daily refillable bottles"
        )

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Maya Angelou", user?.displayName)
        assertEquals("Zero-waste pantry & daily refillable bottles", user?.ecoPledge)
    }
    // endregion

    // region 3. GetDashboardOverviewUseCase Tests
    @Test
    fun getDashboardOverviewUseCase_aggregatesDashboardMetrics() = runTest {
        val fakeUser = User(
            id = "u1",
            displayName = "Alex",
            email = "alex@change360.org",
            ecoPledge = "Solar energy first"
        )
        val fakeChallenge = Challenge(
            id = "energy_1",
            title = "Phantom Power Sweep",
            description = "Unplug vampire appliances",
            category = ChallengeCategory.ENERGY,
            difficulty = ChallengeDifficulty.EASY,
            xpReward = 150,
            co2SavedKg = 1.2f,
            waterSavedLiters = 0f,
            wasteSavedKg = 0f,
            totalSteps = 3,
            completedSteps = 1
        )
        val fakeImpactLog = ImpactLog(
            id = 1L,
            userId = "u1",
            sourceChallengeId = "energy_1",
            actionTitle = "Phantom Power Sweep",
            co2SavedKg = 1.2f,
            waterSavedLiters = 0f,
            wasteSavedKg = 0f,
            loggedAtEpochMillis = System.currentTimeMillis()
        )

        val fakeAuthRepo = FakeAuthRepository(initialUser = fakeUser, initialIsAuth = true)
        val fakeChallengeRepo = FakeChallengeRepository(listOf(fakeChallenge))
        val fakeImpactRepo = FakeImpactRepository(listOf(fakeImpactLog))

        val useCase = GetDashboardOverviewUseCase(fakeAuthRepo, fakeChallengeRepo, fakeImpactRepo)
        val overview = useCase.getSnapshot(maxActiveChallenges = 3)

        assertEquals("Alex", overview.user?.displayName)
        assertEquals(1, overview.activeChallenges.size)
        assertEquals("Phantom Power Sweep", overview.activeChallenges.first().title)
        assertEquals(1.2f, overview.todayImpact.totalCo2SavedKg, 0.01f)
        assertNotNull(overview.dailyTip)
    }
    // endregion

    // region 4. CompleteChallengeUseCase Tests
    @Test
    fun completeChallengeUseCase_advancesStepAndAwardsXp() = runTest {
        val testChallenge = Challenge(
            id = "water_1",
            title = "5-Minute Shower Sprint",
            description = "Shorten showers to conserve water",
            category = ChallengeCategory.WATER,
            difficulty = ChallengeDifficulty.MEDIUM,
            xpReward = 200,
            co2SavedKg = 0.9f,
            waterSavedLiters = 50f,
            wasteSavedKg = 0f,
            totalSteps = 2,
            completedSteps = 1
        )

        val challengeRepo = FakeChallengeRepository(listOf(testChallenge))
        val useCase = CompleteChallengeUseCase(challengeRepo)

        // Log final step (1 -> 2)
        val result = useCase("water_1")
        assertTrue(result.isSuccess)

        val stepResult = result.getOrNull()
        assertNotNull(stepResult)
        assertTrue(stepResult!!.isJustCompleted)
        assertEquals(200, stepResult.xpAwarded)
        assertEquals(50f, stepResult.waterSavedLiters, 0.01f)
        assertEquals(2, stepResult.updatedChallenge.completedSteps)
        assertTrue(stepResult.updatedChallenge.isCompleted)
    }
    // endregion

    // region 5. CalculateImpactStatsUseCase Tests
    @Test
    fun calculateImpactStatsUseCase_computesEquivalentsAndCategories() {
        val impactRepo = FakeImpactRepository(emptyList())
        val useCase = CalculateImpactStatsUseCase(impactRepo)

        val sampleLogs = listOf(
            ImpactLog(
                id = 1L,
                userId = "u1",
                sourceChallengeId = "energy_strip",
                actionTitle = "Unplugged surge strips",
                co2SavedKg = 21.77f,
                waterSavedLiters = 0f,
                wasteSavedKg = 0f
            ),
            ImpactLog(
                id = 2L,
                userId = "u1",
                sourceChallengeId = "waste_bottle",
                actionTitle = "Stainless bottle refill",
                co2SavedKg = 0.5f,
                waterSavedLiters = 10f,
                wasteSavedKg = 2.0f
            )
        )

        val summary = useCase.calculate(sampleLogs, TimeRange.ALL_TIME)

        assertEquals(22.27f, summary.totalCo2SavedKg, 0.01f)
        assertEquals(10f, summary.totalWaterSavedLiters, 0.01f)
        assertEquals(2.0f, summary.totalWasteSavedKg, 0.01f)
        // 21.77 / 21.77 ≈ 1.02 trees
        assertEquals(1.02f, summary.treesPlantedEquivalent, 0.05f)
        // 2 kg waste * 40 = 80 bottles
        assertEquals(80, summary.plasticBottlesSaved)
        // 22.27 * 4.1 ≈ 91.3 km car avoided
        assertEquals(91.3f, summary.carKmAvoided, 0.5f)

        val energyBreakdown = summary.categoryBreakdowns.find { it.category == ChallengeCategory.ENERGY }
        assertNotNull(energyBreakdown)
        assertEquals(21.77f, energyBreakdown!!.co2Kg, 0.01f)
    }
    // endregion

    // region 6. SendAiMessageUseCase Tests
    @Test
    fun sendAiMessageUseCase_validatesAndProcessesPrompt() = runTest {
        val aiRepo = FakeAiAssistantRepository()
        val useCase = SendAiMessageUseCase(aiRepo)

        val emptyResult = useCase("   ")
        assertTrue(emptyResult.isFailure)

        val result = useCase("How can I save water today?")
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertNotNull(response)
        assertEquals(MessageSender.AI, response?.sender)
        assertTrue(response?.content?.isNotBlank() == true)

        val chips = useCase.getQuickPromptChips()
        assertTrue(chips.isNotEmpty())
    }
    // endregion

    // region 7. SaveGeneratedPosterUseCase Tests
    @Test
    fun saveGeneratedPosterUseCase_validatesAndSavesPoster() = runTest {
        val posterRepo = FakePosterRepository()
        val useCase = SaveGeneratedPosterUseCase(posterRepo)

        val blankTitleResult = useCase.invoke(
            title = "",
            pledgeQuote = "Every daily action ripples.",
            templateStyle = PosterAuraStyle.BIO_EMERALD
        )
        assertTrue(blankTitleResult.isFailure)

        val successResult = useCase.invoke(
            title = "Regenerate Earth",
            pledgeQuote = "360 degrees of mindful, climate-positive living.",
            templateStyle = PosterAuraStyle.BIO_EMERALD
        )
        assertTrue(successResult.isSuccess)
        val posterId = successResult.getOrNull()
        assertNotNull(posterId)

        val updateResult = useCase.updateExportedUri(posterId!!, "content://media/external/posters/1.png")
        assertTrue(updateResult.isSuccess)

        val badge = useCase.buildVerifiedBadge(
            user = User(
                id = "u1",
                displayName = "Tara",
                email = "tara@change360.org",
                ecoPledge = "Green transport",
                auraLevel = 3,
                currentStreakDays = 14
            ),
            totalCo2SavedKg = 45.5f
        )
        assertEquals("Tara", badge.practitionerName)
        assertEquals(45.5f, badge.co2SavedKg, 0.01f)
        assertEquals(14, badge.streakDays)
        assertEquals(3, badge.auraLevel)
    }
    // endregion
}

// region Test Doubles / Fakes

private class FakeAuthRepository(
    initialUser: User? = null,
    initialIsAuth: Boolean = false
) : AuthRepository {
    private val userFlow = MutableStateFlow(initialUser)
    private val authFlow = MutableStateFlow(initialIsAuth)

    override fun observeActiveUser(): Flow<User?> = userFlow
    override fun observeIsAuthenticated(): Flow<Boolean> = authFlow
    override fun observeUserPreferences(): Flow<UserPreferencesData> = MutableStateFlow(UserPreferencesData())

    override suspend fun getActiveUser(): User? = userFlow.value
    override suspend fun getUserById(userId: String): User? = userFlow.value?.takeIf { it.id == userId }

    override suspend fun signIn(email: String): Result<User> {
        val user = User(id = "user_sign_in", displayName = "Sign In User", email = email, ecoPledge = "Pledge")
        userFlow.value = user
        authFlow.value = true
        return Result.success(user)
    }

    override suspend fun signUp(displayName: String, email: String, ecoPledge: String): Result<User> {
        val user = User(id = "user_sign_up", displayName = displayName, email = email, ecoPledge = ecoPledge)
        userFlow.value = user
        authFlow.value = true
        return Result.success(user)
    }

    override suspend fun signInAsGuest(displayName: String, ecoPledge: String): Result<User> {
        val user = User(id = "guest_1", displayName = displayName, email = "guest@change360.org", ecoPledge = ecoPledge)
        userFlow.value = user
        authFlow.value = true
        return Result.success(user)
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        userFlow.value = user
        return Result.success(Unit)
    }

    override suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit> {
        userFlow.value = userFlow.value?.copy(ecoPledge = ecoPledge)
        return Result.success(Unit)
    }

    override suspend fun setThemeAuraVariant(variant: String): Result<Unit> = Result.success(Unit)

    override suspend fun signOut(): Result<Unit> {
        userFlow.value = null
        authFlow.value = false
        return Result.success(Unit)
    }
}

private class FakeChallengeRepository(
    initialChallenges: List<Challenge> = emptyList()
) : ChallengeRepository {
    private val challenges = initialChallenges.toMutableList()
    private val flow = MutableStateFlow(challenges.toList())

    override fun observeAllChallenges(): Flow<List<Challenge>> = flow
    override fun observeChallengesByCategory(category: ChallengeCategory): Flow<List<Challenge>> = flow
    override fun observeActiveChallenges(): Flow<List<Challenge>> = flow
    override fun observeCompletedChallenges(): Flow<List<Challenge>> = flow
    override fun observeChallengeById(id: String): Flow<Challenge?> = MutableStateFlow(challenges.find { it.id == id })
    override suspend fun getChallengeById(id: String): Challenge? = challenges.find { it.id == id }
    override fun observeCompletedCount(): Flow<Int> = MutableStateFlow(challenges.count { it.isCompleted })
    override fun observeTotalCount(): Flow<Int> = MutableStateFlow(challenges.size)

    override suspend fun logStep(challengeId: String): Result<ChallengeStepResult> {
        val index = challenges.indexOfFirst { it.id == challengeId }
        if (index == -1) return Result.failure(NoSuchElementException())
        val current = challenges[index]
        val nextSteps = (current.completedSteps + 1).coerceAtMost(current.totalSteps)
        val isCompleted = nextSteps >= current.totalSteps
        val updated = current.copy(
            completedSteps = nextSteps,
            isCompleted = isCompleted,
            completedAtEpochMillis = if (isCompleted) System.currentTimeMillis() else null
        )
        challenges[index] = updated
        flow.value = challenges.toList()
        return Result.success(
            ChallengeStepResult(
                updatedChallenge = updated,
                isJustCompleted = isCompleted,
                xpAwarded = if (isCompleted) current.xpReward else 0,
                co2SavedKg = if (isCompleted) current.co2SavedKg else 0f,
                waterSavedLiters = if (isCompleted) current.waterSavedLiters else 0f,
                wasteSavedKg = if (isCompleted) current.wasteSavedKg else 0f
            )
        )
    }

    override suspend fun resetChallenge(challengeId: String): Result<Unit> {
        val index = challenges.indexOfFirst { it.id == challengeId }
        if (index != -1) {
            challenges[index] = challenges[index].copy(completedSteps = 0, isCompleted = false)
            flow.value = challenges.toList()
        }
        return Result.success(Unit)
    }

    override suspend fun upsertChallenge(challenge: Challenge): Result<Unit> = Result.success(Unit)
    override suspend fun upsertChallenges(challenges: List<Challenge>): Result<Unit> = Result.success(Unit)
}

private class FakeImpactRepository(
    initialLogs: List<ImpactLog> = emptyList()
) : ImpactRepository {
    private val logs = initialLogs.toMutableList()
    private val flow = MutableStateFlow(logs.toList())

    override fun observeAllLogs(): Flow<List<ImpactLog>> = flow
    override fun observeLogsForUser(userId: String): Flow<List<ImpactLog>> = flow
    override fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLog>> = flow
    override fun observeImpactSummary(timeRange: TimeRange): Flow<ImpactSummary> =
        MutableStateFlow(ImpactSummary.calculate(logs, timeRange))

    override fun observeTotalCo2Saved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.co2SavedKg.toDouble() }.toFloat())
    override fun observeTotalWaterSaved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.waterSavedLiters.toDouble() }.toFloat())
    override fun observeTotalWasteSaved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.wasteSavedKg.toDouble() }.toFloat())

    override suspend fun logImpact(
        actionTitle: String,
        co2SavedKg: Float,
        waterSavedLiters: Float,
        wasteSavedKg: Float,
        sourceChallengeId: String?
    ): Result<Long> {
        val id = logs.size.toLong() + 1
        logs.add(
            ImpactLog(
                id = id,
                userId = "u1",
                sourceChallengeId = sourceChallengeId,
                actionTitle = actionTitle,
                co2SavedKg = co2SavedKg,
                waterSavedLiters = waterSavedLiters,
                wasteSavedKg = wasteSavedKg
            )
        )
        flow.value = logs.toList()
        return Result.success(id)
    }

    override suspend fun deleteLogById(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun clearAllLogs(): Result<Unit> = Result.success(Unit)
}

private class FakeAiAssistantRepository : AiAssistantRepository {
    private val messages = mutableListOf<ChatMessage>()
    private val flow = MutableStateFlow<List<ChatMessage>>(emptyList())

    override fun observeConversation(): Flow<List<ChatMessage>> = flow
    override fun observeRecentMessages(limit: Int): Flow<List<ChatMessage>> = flow

    override suspend fun sendMessage(userPrompt: String): Result<ChatMessage> {
        val now = System.currentTimeMillis()
        messages.add(ChatMessage(id = 1L, sender = MessageSender.USER, content = userPrompt, timestampEpochMillis = now))
        val aiMessage = ChatMessage(
            id = 2L,
            sender = MessageSender.AI,
            content = "Tailored advice for: $userPrompt",
            suggestedActionCategory = ChallengeCategory.WATER,
            timestampEpochMillis = now + 100L
        )
        messages.add(aiMessage)
        flow.value = messages.toList()
        return Result.success(aiMessage)
    }

    override suspend fun clearHistory(): Result<Unit> {
        messages.clear()
        flow.value = emptyList()
        return Result.success(Unit)
    }

    override suspend fun deleteMessageById(id: Long): Result<Unit> = Result.success(Unit)
    override fun getQuickPromptChips(): List<String> = listOf("Calculate commute footprint", "5-minute zero-waste swap")
}

private class FakePosterRepository : PosterRepository {
    private val posters = mutableListOf<SavedPoster>()
    private val flow = MutableStateFlow<List<SavedPoster>>(emptyList())

    override fun observeSavedPosters(): Flow<List<SavedPoster>> = flow
    override fun observePosterById(id: Long): Flow<SavedPoster?> = MutableStateFlow(posters.find { it.id == id })
    override suspend fun getPosterById(id: Long): SavedPoster? = posters.find { it.id == id }
    override fun getCuratedTemplates(): List<PosterTemplate> = emptyList()

    override suspend fun savePoster(
        title: String,
        pledgeQuote: String,
        templateStyle: String,
        accentColorHex: String,
        exportedUriString: String?
    ): Result<Long> {
        val id = posters.size.toLong() + 1
        posters.add(
            SavedPoster(
                id = id,
                title = title,
                pledgeQuote = pledgeQuote,
                templateStyle = PosterAuraStyle.fromKey(templateStyle),
                accentColorHex = accentColorHex,
                exportedUriString = exportedUriString
            )
        )
        flow.value = posters.toList()
        return Result.success(id)
    }

    override suspend fun updateExportedUri(id: Long, exportedUriString: String): Result<Unit> {
        val index = posters.indexOfFirst { it.id == id }
        if (index != -1) {
            posters[index] = posters[index].copy(exportedUriString = exportedUriString)
            flow.value = posters.toList()
        }
        return Result.success(Unit)
    }

    override suspend fun deletePosterById(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun clearAllPosters(): Result<Unit> = Result.success(Unit)
}
// endregion

