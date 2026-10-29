package org.change360.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.change360.app.data.local.Change360Database
import org.change360.app.data.local.datastore.UserPreferencesData
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.domain.model.Challenge
import org.change360.app.domain.model.ChallengeCategory
import org.change360.app.domain.model.ChallengeStepResult
import org.change360.app.domain.model.ChatMessage
import org.change360.app.domain.model.ImpactLog
import org.change360.app.domain.model.ImpactSummary
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Unit tests verifying Dependency Injection container contract and ViewModelFactory wiring.
 */
class DependencyInjectionTest {

    private val fakeContainer = TestAppContainer()

    // region 1. Reflection-based Constructor Resolution
    @Test
    fun viewModelFactory_instantiatesViewModelWithUseCaseDependency() {
        val factory = ViewModelFactory(fakeContainer)
        val vm = factory.create(TestSplashViewModel::class.java)

        assertNotNull(vm)
        assertSame(fakeContainer.observeAuthStateUseCase, vm.observeAuthStateUseCase)
    }

    @Test
    fun viewModelFactory_instantiatesViewModelWithMultipleDependencies() {
        val factory = ViewModelFactory(fakeContainer)
        val vm = factory.create(TestAuthViewModel::class.java)

        assertNotNull(vm)
        assertSame(fakeContainer.authenticateUserUseCase, vm.authenticateUserUseCase)
        assertSame(fakeContainer.authRepository, vm.authRepository)
    }

    @Test
    fun viewModelFactory_instantiatesViewModelWithAppContainerDirectly() {
        val factory = ViewModelFactory(fakeContainer)
        val vm = factory.create(TestContainerViewModel::class.java)

        assertNotNull(vm)
        assertSame(fakeContainer, vm.container)
    }

    @Test
    fun viewModelFactory_instantiatesNoArgViewModel() {
        val factory = ViewModelFactory(fakeContainer)
        val vm = factory.create(TestNoArgViewModel::class.java)

        assertNotNull(vm)
    }

    @Test
    fun viewModelFactory_throwsException_whenDependenciesCannotBeSatisfied() {
        val factory = ViewModelFactory(fakeContainer)
        try {
            factory.create(TestUnsatisfiedViewModel::class.java)
            fail("Expected IllegalArgumentException for unsatisfied dependencies")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Cannot construct ViewModel instance") == true)
        }
    }
    // endregion

    // region 2. Custom Creators & Builder Tests
    @Test
    fun viewModelFactory_usesExplicitCreator_takingPrecedenceOverReflection() {
        val customInstance = TestSplashViewModel(fakeContainer.observeAuthStateUseCase)
        val creators = mapOf<Class<out ViewModel>, (AppContainer) -> ViewModel>(
            TestSplashViewModel::class.java to { customInstance }
        )
        val factory = ViewModelFactory(fakeContainer, creators)
        val resolved = factory.create(TestSplashViewModel::class.java)

        assertSame(customInstance, resolved)
    }

    @Test
    fun viewModelFactoryBuilder_registersCustomCreatorsCorrectly() {
        val customDashboardVm = TestDashboardViewModel(fakeContainer.getDashboardOverviewUseCase)

        val factory = ViewModelFactory.builder(fakeContainer)
            .addCreator<TestDashboardViewModel> { customDashboardVm }
            .build()

        val resolved = factory.create(TestDashboardViewModel::class.java)
        assertSame(customDashboardVm, resolved)
    }

    @Test
    fun provideViewModelFactory_createsDedicatedFactoryForSingleViewModel() {
        val expectedVm = TestNoArgViewModel()
        val factory = provideViewModelFactory(fakeContainer) { expectedVm }

        val created = factory.create(TestNoArgViewModel::class.java)
        assertSame(expectedVm, created)

        try {
            factory.create(TestSplashViewModel::class.java)
            fail("Expected IllegalArgumentException for mismatched ViewModel type")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Expected ViewModel of type") == true)
        }
    }

    @Test
    fun appContainerExtension_createViewModelFactory_buildsWorkingFactory() {
        val factory = fakeContainer.createViewModelFactory(
            TestNoArgViewModel::class.java to { TestNoArgViewModel() }
        )
        val created = factory.create(TestNoArgViewModel::class.java)
        assertNotNull(created)
    }
    // endregion
}

// region Test ViewModels
class TestSplashViewModel(val observeAuthStateUseCase: ObserveAuthStateUseCase) : ViewModel()
class TestAuthViewModel(val authenticateUserUseCase: AuthenticateUserUseCase, val authRepository: AuthRepository) : ViewModel()
class TestDashboardViewModel(val getDashboardOverviewUseCase: GetDashboardOverviewUseCase) : ViewModel()
class TestContainerViewModel(val container: AppContainer) : ViewModel()
class TestNoArgViewModel : ViewModel()
class TestUnsatisfiedViewModel(val unresolvableString: String) : ViewModel()
// endregion

// region Test AppContainer Implementation
class TestAppContainer : AppContainer {
    override val database: Change360Database
        get() = throw UnsupportedOperationException("Database mock not required for factory unit tests")

    override val userPreferencesDataSource: UserPreferencesDataSource
        get() = throw UnsupportedOperationException("DataStore mock not required for factory unit tests")

    override val authRepository: AuthRepository = FakeTestAuthRepository()
    override val challengeRepository: ChallengeRepository = FakeTestChallengeRepository()
    override val impactRepository: ImpactRepository = FakeTestImpactRepository()
    override val aiAssistantRepository: AiAssistantRepository = FakeTestAiAssistantRepository()
    override val posterRepository: PosterRepository = FakeTestPosterRepository()

    override val observeAuthStateUseCase: ObserveAuthStateUseCase = ObserveAuthStateUseCase(authRepository)
    override val authenticateUserUseCase: AuthenticateUserUseCase = AuthenticateUserUseCase(authRepository)
    override val getDashboardOverviewUseCase: GetDashboardOverviewUseCase =
        GetDashboardOverviewUseCase(authRepository, challengeRepository, impactRepository)
    override val completeChallengeUseCase: CompleteChallengeUseCase = CompleteChallengeUseCase(challengeRepository)
    override val calculateImpactStatsUseCase: CalculateImpactStatsUseCase = CalculateImpactStatsUseCase(impactRepository)
    override val sendAiMessageUseCase: SendAiMessageUseCase = SendAiMessageUseCase(aiAssistantRepository)
    override val saveGeneratedPosterUseCase: SaveGeneratedPosterUseCase = SaveGeneratedPosterUseCase(posterRepository)
}

private class FakeTestAuthRepository : AuthRepository {
    override fun observeActiveUser(): Flow<User?> = MutableStateFlow(null)
    override fun observeIsAuthenticated(): Flow<Boolean> = MutableStateFlow(false)
    override fun observeUserPreferences(): Flow<UserPreferencesData> = MutableStateFlow(UserPreferencesData())
    override suspend fun getActiveUser(): User? = null
    override suspend fun getUserById(userId: String): User? = null
    override suspend fun signIn(email: String): Result<User> = Result.failure(NotImplementedError())
    override suspend fun signUp(displayName: String, email: String, ecoPledge: String): Result<User> = Result.failure(NotImplementedError())
    override suspend fun signInAsGuest(displayName: String, ecoPledge: String): Result<User> = Result.failure(NotImplementedError())
    override suspend fun updateUser(user: User): Result<Unit> = Result.success(Unit)
    override suspend fun updateEcoPledge(userId: String, ecoPledge: String): Result<Unit> = Result.success(Unit)
    override suspend fun setThemeAuraVariant(variant: String): Result<Unit> = Result.success(Unit)
    override suspend fun signOut(): Result<Unit> = Result.success(Unit)
}

private class FakeTestChallengeRepository : ChallengeRepository {
    override fun observeAllChallenges(): Flow<List<Challenge>> = MutableStateFlow(emptyList())
    override fun observeChallengesByCategory(category: ChallengeCategory): Flow<List<Challenge>> = MutableStateFlow(emptyList())
    override fun observeActiveChallenges(): Flow<List<Challenge>> = MutableStateFlow(emptyList())
    override fun observeCompletedChallenges(): Flow<List<Challenge>> = MutableStateFlow(emptyList())
    override fun observeChallengeById(id: String): Flow<Challenge?> = MutableStateFlow(null)
    override suspend fun getChallengeById(id: String): Challenge? = null
    override fun observeCompletedCount(): Flow<Int> = MutableStateFlow(0)
    override fun observeTotalCount(): Flow<Int> = MutableStateFlow(0)
    override suspend fun logStep(challengeId: String): Result<ChallengeStepResult> = Result.failure(NotImplementedError())
    override suspend fun resetChallenge(challengeId: String): Result<Unit> = Result.success(Unit)
    override suspend fun upsertChallenge(challenge: Challenge): Result<Unit> = Result.success(Unit)
    override suspend fun upsertChallenges(challenges: List<Challenge>): Result<Unit> = Result.success(Unit)
}

private class FakeTestImpactRepository : ImpactRepository {
    override fun observeAllLogs(): Flow<List<ImpactLog>> = MutableStateFlow(emptyList())
    override fun observeLogsForUser(userId: String): Flow<List<ImpactLog>> = MutableStateFlow(emptyList())
    override fun observeLogsSince(sinceEpochMillis: Long): Flow<List<ImpactLog>> = MutableStateFlow(emptyList())
    override fun observeImpactSummary(timeRange: TimeRange): Flow<ImpactSummary> = MutableStateFlow(ImpactSummary.calculate(emptyList(), timeRange))
    override fun observeTotalCo2Saved(): Flow<Float> = MutableStateFlow(0f)
    override fun observeTotalWaterSaved(): Flow<Float> = MutableStateFlow(0f)
    override fun observeTotalWasteSaved(): Flow<Float> = MutableStateFlow(0f)
    override suspend fun logImpact(actionTitle: String, co2SavedKg: Float, waterSavedLiters: Float, wasteSavedKg: Float, sourceChallengeId: String?): Result<Long> = Result.success(1L)
    override suspend fun deleteLogById(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun clearAllLogs(): Result<Unit> = Result.success(Unit)
}

private class FakeTestAiAssistantRepository : AiAssistantRepository {
    override fun observeConversation(): Flow<List<ChatMessage>> = MutableStateFlow(emptyList())
    override fun observeRecentMessages(limit: Int): Flow<List<ChatMessage>> = MutableStateFlow(emptyList())
    override suspend fun sendMessage(userPrompt: String): Result<ChatMessage> = Result.failure(NotImplementedError())
    override suspend fun clearHistory(): Result<Unit> = Result.success(Unit)
    override suspend fun deleteMessageById(id: Long): Result<Unit> = Result.success(Unit)
    override fun getQuickPromptChips(): List<String> = emptyList()
}

private class FakeTestPosterRepository : PosterRepository {
    override fun observeSavedPosters(): Flow<List<SavedPoster>> = MutableStateFlow(emptyList())
    override fun observePosterById(id: Long): Flow<SavedPoster?> = MutableStateFlow(null)
    override suspend fun getPosterById(id: Long): SavedPoster? = null
    override fun getCuratedTemplates(): List<PosterTemplate> = emptyList()
    override suspend fun savePoster(title: String, pledgeQuote: String, templateStyle: String, accentColorHex: String, exportedUriString: String?): Result<Long> = Result.success(1L)
    override suspend fun updateExportedUri(id: Long, exportedUriString: String): Result<Unit> = Result.success(Unit)
    override suspend fun deletePosterById(id: Long): Result<Unit> = Result.success(Unit)
    override suspend fun clearAllPosters(): Result<Unit> = Result.success(Unit)
}
// endregion

