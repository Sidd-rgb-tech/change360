package org.change360.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.change360.app.data.local.Change360Database
import org.change360.app.data.local.datastore.UserPreferencesData
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.di.AppContainer
import org.change360.app.di.ViewModelFactory
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
import org.change360.app.domain.usecase.AuthenticateUserUseCase
import org.change360.app.domain.usecase.CalculateImpactStatsUseCase
import org.change360.app.domain.usecase.CompleteChallengeUseCase
import org.change360.app.domain.usecase.GetDashboardOverviewUseCase
import org.change360.app.domain.usecase.ObserveAuthStateUseCase
import org.change360.app.domain.usecase.SaveGeneratedPosterUseCase
import org.change360.app.domain.usecase.SendAiMessageUseCase
import org.change360.app.ui.screens.ai.AiAssistantViewModel
import org.change360.app.ui.screens.auth.AuthMode
import org.change360.app.ui.screens.auth.AuthViewModel
import org.change360.app.ui.screens.challenges.ChallengesViewModel
import org.change360.app.ui.screens.home.HomeViewModel
import org.change360.app.ui.screens.impact.ImpactViewModel
import org.change360.app.ui.screens.poster.PosterViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying behavior, state flow emissions, and [ViewModelFactory] constructor
 * resolution for all 6 CHANGE360 ViewModels:
 * 1. [AuthViewModel]
 * 2. [HomeViewModel]
 * 3. [AiAssistantViewModel]
 * 4. [ChallengesViewModel]
 * 5. [ImpactViewModel]
 * 6. [PosterViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeContainer: FakeAppContainer
    private lateinit var factory: ViewModelFactory

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeContainer = FakeAppContainer()
        factory = ViewModelFactory(fakeContainer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region 1. ViewModelFactory Reflection Resolution Tests
    @Test
    fun viewModelFactory_resolvesAllSixViewModels() {
        val authVm = factory.create(AuthViewModel::class.java)
        assertNotNull(authVm)

        val homeVm = factory.create(HomeViewModel::class.java)
        assertNotNull(homeVm)

        val aiVm = factory.create(AiAssistantViewModel::class.java)
        assertNotNull(aiVm)

        val challengesVm = factory.create(ChallengesViewModel::class.java)
        assertNotNull(challengesVm)

        val impactVm = factory.create(ImpactViewModel::class.java)
        assertNotNull(impactVm)

        val posterVm = factory.create(PosterViewModel::class.java)
        assertNotNull(posterVm)
    }
    // endregion

    // region 2. AuthViewModel Tests
    @Test
    fun authViewModel_managesModeAndFormChanges() {
        val vm = factory.create(AuthViewModel::class.java)

        assertEquals(AuthMode.SIGN_UP, vm.uiState.value.mode)

        vm.onModeChanged(AuthMode.SIGN_IN)
        assertEquals(AuthMode.SIGN_IN, vm.uiState.value.mode)

        vm.onDisplayNameChanged("Gaia Champion")
        assertEquals("Gaia Champion", vm.uiState.value.displayName)

        vm.onEmailChanged("guardian@change360.org")
        assertEquals("guardian@change360.org", vm.uiState.value.email)

        vm.onPledgeSelected("Slash household vampire electricity")
        assertEquals("Slash household vampire electricity", vm.uiState.value.selectedPledge)

        vm.onCustomPledgeChanged("Custom regenerative pledge")
        assertEquals("Custom regenerative pledge", vm.uiState.value.activePledge)
    }

    @Test
    fun authViewModel_handlesGuestAuthentication() = runTest(testDispatcher) {
        val vm = factory.create(AuthViewModel::class.java)
        vm.onModeChanged(AuthMode.GUEST)
        vm.onDisplayNameChanged("Eco Pilgrim")

        vm.submit()
        testScheduler.advanceUntilIdle()

        assertTrue(vm.uiState.value.isSuccess)
        assertNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun authViewModel_handlesSignUpValidationFailure() = runTest(testDispatcher) {
        val vm = factory.create(AuthViewModel::class.java)
        vm.onDisplayNameChanged("A") // invalid length (< 2)
        vm.onEmailChanged("invalid-email")

        vm.submit()
        testScheduler.advanceUntilIdle()

        assertFalse(vm.uiState.value.isSuccess)
        assertNotNull(vm.uiState.value.errorMessage)
    }
    // endregion

    // region 3. HomeViewModel Tests
    @Test
    fun homeViewModel_exposesOverviewAndHandlesQuestSteps() = runTest(testDispatcher) {
        val vm = factory.create(HomeViewModel::class.java)
        testScheduler.advanceUntilIdle()

        val initialOverview = vm.overview.value
        assertEquals("Earth Guardian", initialOverview.user?.displayName)
        assertEquals(1, initialOverview.activeChallenges.size)

        // Log quest step
        vm.logQuestStep("energy_quest_1")
        testScheduler.advanceUntilIdle()

        val updatedState = vm.uiState.value
        assertFalse(updatedState.isLoggingStep)
        assertNull(updatedState.errorMessage)
    }
    // endregion

    // region 4. AiAssistantViewModel Tests
    @Test
    fun aiAssistantViewModel_managesPromptInputAndQuickChipDispatch() = runTest(testDispatcher) {
        val vm = factory.create(AiAssistantViewModel::class.java)
        testScheduler.advanceUntilIdle()

        assertTrue(vm.uiState.value.quickChips.isNotEmpty())

        vm.onInputPromptChanged("How to reduce plastic packaging?")
        assertEquals("How to reduce plastic packaging?", vm.uiState.value.inputPrompt)
        assertTrue(vm.uiState.value.isSendEnabled)

        // Send prompt
        vm.sendMessage()
        testScheduler.advanceUntilIdle()

        assertEquals("", vm.uiState.value.inputPrompt)
        assertFalse(vm.uiState.value.isTyping)
        assertEquals(2, vm.uiState.value.messages.size)

        // Dispatch prompt chip
        vm.onQuickPromptSelected("5-minute zero-waste swap", autoSend = true)
        testScheduler.advanceUntilIdle()

        assertEquals(4, vm.uiState.value.messages.size)
    }
    // endregion

    // region 5. ChallengesViewModel Tests
    @Test
    fun challengesViewModel_managesCategoryFiltersAndCompletion() = runTest(testDispatcher) {
        val vm = factory.create(ChallengesViewModel::class.java)
        testScheduler.advanceUntilIdle()

        assertEquals(ChallengeCategory.ALL, vm.uiState.value.selectedCategory)
        assertEquals(2, vm.uiState.value.challenges.size)

        // Filter by WATER category
        vm.onCategorySelected(ChallengeCategory.WATER)
        testScheduler.advanceUntilIdle()

        assertEquals(1, vm.uiState.value.challenges.size)
        assertEquals(ChallengeCategory.WATER, vm.uiState.value.challenges.first().category)

        // Complete final step to trigger celebration
        vm.logChallengeStep("water_quest_1")
        testScheduler.advanceUntilIdle()

        val celebration = vm.uiState.value.celebrationEvent
        assertNotNull(celebration)
        assertTrue(celebration!!.isJustCompleted)
        assertEquals(150, celebration.xpAwarded)

        vm.clearCelebration()
        assertNull(vm.uiState.value.celebrationEvent)
    }
    // endregion

    // region 6. ImpactViewModel Tests
    @Test
    fun impactViewModel_managesTimeRangeAndAggregatesMetrics() = runTest(testDispatcher) {
        val vm = factory.create(ImpactViewModel::class.java)
        testScheduler.advanceUntilIdle()

        assertEquals(TimeRange.WEEK, vm.uiState.value.timeRange)
        assertNotNull(vm.uiState.value.analytics)
        assertTrue(vm.uiState.value.recentLogs.isNotEmpty())

        // Switch to ALL_TIME
        vm.onTimeRangeSelected(TimeRange.ALL_TIME)
        testScheduler.advanceUntilIdle()

        assertEquals(TimeRange.ALL_TIME, vm.uiState.value.timeRange)
    }
    // endregion

    // region 7. PosterViewModel Tests
    @Test
    fun posterViewModel_managesStyleCustomizationAndExportPipeline() = runTest(testDispatcher) {
        val vm = factory.create(PosterViewModel::class.java)
        testScheduler.advanceUntilIdle()

        assertEquals(PosterAuraStyle.BIO_EMERALD, vm.uiState.value.selectedStyle)

        // Change style
        vm.onStyleSelected(PosterAuraStyle.SOLAR_FLARE)
        assertEquals(PosterAuraStyle.SOLAR_FLARE, vm.uiState.value.selectedStyle)
        assertEquals(PosterAuraStyle.SOLAR_FLARE.primaryColorHex, vm.uiState.value.accentColorHex)

        // Edit headline & quote
        vm.onHeadlineChanged("Sun Powered Future")
        vm.onQuoteChanged("Solar energy for every neighborhood.")
        assertEquals("Sun Powered Future", vm.uiState.value.headline)

        // Save poster
        vm.savePoster()
        testScheduler.advanceUntilIdle()

        assertNotNull(vm.uiState.value.lastSavedPosterId)
        assertFalse(vm.uiState.value.isSaving)

        // Export pipeline
        vm.startExport()
        assertTrue(vm.uiState.value.isExporting)

        vm.finishExport("content://media/external/posters/test.png")
        assertFalse(vm.uiState.value.isExporting)
        assertEquals("content://media/external/posters/test.png", vm.uiState.value.lastExportedUri)
    }
    // endregion
}

// region Test Doubles & Fake AppContainer
private class FakeAppContainer : AppContainer {
    override val database: Change360Database
        get() = throw UnsupportedOperationException("Database mock not required for unit tests")

    override val userPreferencesDataSource: UserPreferencesDataSource
        get() = throw UnsupportedOperationException("DataStore mock not required for unit tests")

    override val authRepository: AuthRepository = FakeAuthRepo()
    override val challengeRepository: ChallengeRepository = FakeChallengeRepo()
    override val impactRepository: ImpactRepository = FakeImpactRepo()
    override val aiAssistantRepository: AiAssistantRepository = FakeAiRepo()
    override val posterRepository: PosterRepository = FakePosterRepo()

    override val observeAuthStateUseCase: ObserveAuthStateUseCase = ObserveAuthStateUseCase(authRepository)
    override val authenticateUserUseCase: AuthenticateUserUseCase = AuthenticateUserUseCase(authRepository)
    override val getDashboardOverviewUseCase: GetDashboardOverviewUseCase =
        GetDashboardOverviewUseCase(authRepository, challengeRepository, impactRepository)
    override val completeChallengeUseCase: CompleteChallengeUseCase = CompleteChallengeUseCase(challengeRepository)
    override val calculateImpactStatsUseCase: CalculateImpactStatsUseCase = CalculateImpactStatsUseCase(impactRepository)
    override val sendAiMessageUseCase: SendAiMessageUseCase = SendAiMessageUseCase(aiAssistantRepository)
    override val saveGeneratedPosterUseCase: SaveGeneratedPosterUseCase = SaveGeneratedPosterUseCase(posterRepository)
}

private class FakeAuthRepo : AuthRepository {
    private val currentUser = MutableStateFlow<User?>(
        User(
            id = "u1",
            displayName = "Earth Guardian",
            email = "guardian@change360.org",
            ecoPledge = "Cut single-use plastics",
            auraLevel = 2,
            xpPoints = 600,
            currentStreakDays = 7
        )
    )
    private val isAuth = MutableStateFlow(true)

    override fun observeActiveUser(): Flow<User?> = currentUser
    override fun observeIsAuthenticated(): Flow<Boolean> = isAuth
    override fun observeUserPreferences(): Flow<UserPreferencesData> = MutableStateFlow(UserPreferencesData())
    override suspend fun getActiveUser(): User? = currentUser.value
    override suspend fun getUserById(userId: String): User? = currentUser.value

    override suspend fun signIn(email: String): Result<User> {
        val user = User(id = "u1", displayName = "Earth Guardian", email = email, ecoPledge = "Pledge")
        currentUser.value = user
        isAuth.value = true
        return Result.success(user)
    }

    override suspend fun signUp(displayName: String, email: String, ecoPledge: String): Result<User> {
        val user = User(id = "u_new", displayName = displayName, email = email, ecoPledge = ecoPledge)
        currentUser.value = user
        isAuth.value = true
        return Result.success(user)
    }

    override suspend fun signInAsGuest(displayName: String, ecoPledge: String): Result<User> {
        val user = User(id = "u_guest", displayName = displayName, email = "guest@change360.org", ecoPledge = ecoPledge)
        currentUser.value = user
        isAuth.value = true
        return Result.success(user)
    }

    override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)
    override suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit> = Result.success(Unit)
    override suspend fun setThemeAuraVariant(variant: String): Result<Unit> = Result.success(Unit)
    override suspend fun signOut(): Result<Unit> {
        currentUser.value = null
        isAuth.value = false
        return Result.success(Unit)
    }
}

private class FakeChallengeRepo : ChallengeRepository {
    private val challenges = mutableListOf(
        Challenge(
            id = "energy_quest_1",
            title = "Phantom Power Sweep",
            description = "Unplug idle electronics",
            category = ChallengeCategory.ENERGY,
            difficulty = ChallengeDifficulty.EASY,
            xpReward = 100,
            co2SavedKg = 1.0f,
            waterSavedLiters = 0f,
            wasteSavedKg = 0f,
            totalSteps = 3,
            completedSteps = 1,
            isCompleted = false
        ),
        Challenge(
            id = "water_quest_1",
            title = "Short Shower Sprint",
            description = "Shower under 5 minutes",
            category = ChallengeCategory.WATER,
            difficulty = ChallengeDifficulty.MEDIUM,
            xpReward = 150,
            co2SavedKg = 0.5f,
            waterSavedLiters = 35f,
            wasteSavedKg = 0f,
            totalSteps = 1,
            completedSteps = 0,
            isCompleted = false
        )
    )
    private val flow = MutableStateFlow(challenges.toList())

    override fun observeAllChallenges(): Flow<List<Challenge>> = flow
    override fun observeChallengesByCategory(category: ChallengeCategory): Flow<List<Challenge>> = flow
    override fun observeActiveChallenges(): Flow<List<Challenge>> =
        flow.map { list -> list.filter { !it.isCompleted && it.completedSteps > 0 } }
    override fun observeCompletedChallenges(): Flow<List<Challenge>> = flow
    override fun observeChallengeById(id: String): Flow<Challenge?> = MutableStateFlow(challenges.find { it.id == id })
    override suspend fun getChallengeById(id: String): Challenge? = challenges.find { it.id == id }
    override fun observeCompletedCount(): Flow<Int> = MutableStateFlow(challenges.count { it.isCompleted })
    override fun observeTotalCount(): Flow<Int> = MutableStateFlow(challenges.size)

    override suspend fun logStep(challengeId: String): Result<ChallengeStepResult> {
        val index = challenges.indexOfFirst { it.id == challengeId }
        if (index == -1) return Result.failure(NoSuchElementException())
        val c = challenges[index]
        val next = (c.completedSteps + 1).coerceAtMost(c.totalSteps)
        val completed = next >= c.totalSteps
        val updated = c.copy(completedSteps = next, isCompleted = completed)
        challenges[index] = updated
        flow.value = challenges.toList()
        return Result.success(
            ChallengeStepResult(
                updatedChallenge = updated,
                isJustCompleted = completed,
                xpAwarded = if (completed) c.xpReward else 0,
                co2SavedKg = if (completed) c.co2SavedKg else 0f,
                waterSavedLiters = if (completed) c.waterSavedLiters else 0f,
                wasteSavedKg = if (completed) c.wasteSavedKg else 0f
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

private class FakeImpactRepo : ImpactRepository {
    private val logs = mutableListOf(
        ImpactLog(
            id = 1L,
            userId = "u1",
            sourceChallengeId = "energy_quest_1",
            actionTitle = "Unplugged vampire appliances",
            co2SavedKg = 1.0f,
            waterSavedLiters = 0f,
            wasteSavedKg = 0f
        )
    )
    private val flow = MutableStateFlow(logs.toList())

    override fun observeAllLogs(): Flow<List<ImpactLog>> = flow
    override fun observeLogsForUser(userId: String): Flow<List<ImpactLog>> = flow
    override fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLog>> = flow
    override fun observeImpactSummary(timeRange: TimeRange): Flow<ImpactSummary> =
        MutableStateFlow(ImpactSummary.calculate(logs, timeRange))

    override fun observeTotalCo2Saved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.co2SavedKg.toDouble() }.toFloat())
    override fun observeTotalWaterSaved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.waterSavedLiters.toDouble() }.toFloat())
    override fun observeTotalWasteSaved(): Flow<Float> = MutableStateFlow(logs.sumOf { it.wasteSavedKg.toDouble() }.toFloat())

    override suspend fun logImpact(actionTitle: String, co2SavedKg: Float, waterSavedLiters: Float, wasteSavedKg: Float, sourceChallengeId: String?): Result<Long> = Result.success(1L)
    override suspend fun deleteLogById(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun clearAllLogs(): Result<Unit> = Result.success(Unit)
}

private class FakeAiRepo : AiAssistantRepository {
    private val messages = mutableListOf<ChatMessage>()
    private val flow = MutableStateFlow<List<ChatMessage>>(emptyList())

    override fun observeConversation(): Flow<List<ChatMessage>> = flow
    override fun observeRecentMessages(limit: Int): Flow<List<ChatMessage>> = flow

    override suspend fun sendMessage(userPrompt: String): Result<ChatMessage> {
        val now = System.currentTimeMillis()
        val userMsg = ChatMessage(id = messages.size.toLong() + 1, sender = MessageSender.USER, content = userPrompt, timestampEpochMillis = now)
        val aiMsg = ChatMessage(id = messages.size.toLong() + 2, sender = MessageSender.AI, content = "Eco guidance: $userPrompt", timestampEpochMillis = now + 1)
        messages.addAll(listOf(userMsg, aiMsg))
        flow.value = messages.toList()
        return Result.success(aiMsg)
    }

    override suspend fun clearHistory(): Result<Unit> {
        messages.clear()
        flow.value = emptyList()
        return Result.success(Unit)
    }

    override suspend fun deleteMessageById(id: Long): Result<Unit> = Result.success(Unit)
    override fun getQuickPromptChips(): List<String> = listOf("Calculate commute footprint", "5-minute zero-waste swap")
}

private class FakePosterRepo : PosterRepository {
    private val posters = mutableListOf<SavedPoster>()
    private val flow = MutableStateFlow<List<SavedPoster>>(emptyList())

    override fun observeSavedPosters(): Flow<List<SavedPoster>> = flow
    override fun observePosterById(id: Long): Flow<SavedPoster?> = MutableStateFlow(posters.find { it.id == id })
    override suspend fun getPosterById(id: Long): SavedPoster? = posters.find { it.id == id }
    override fun getCuratedTemplates(): List<PosterTemplate> = listOf(
        PosterTemplate("t1", "Bio Pulse", "For Earth.", PosterAuraStyle.BIO_EMERALD, PosterAuraStyle.BIO_EMERALD.primaryColorHex)
    )

    override suspend fun savePoster(title: String, pledgeQuote: String, templateStyle: String, accentColorHex: String, exportedUriString: String?): Result<Long> {
        val id = posters.size.toLong() + 1
        posters.add(SavedPoster(id, title, pledgeQuote, PosterAuraStyle.fromKey(templateStyle), accentColorHex, exportedUriString))
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

