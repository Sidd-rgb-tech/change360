package org.change360.app.di

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.change360.app.data.local.Change360Database
import org.change360.app.data.local.datastore.UserPreferencesDataSource
import org.change360.app.data.local.datastore.change360DataStore
import org.change360.app.data.repository.AiAssistantRepositoryImpl
import org.change360.app.data.repository.AuthRepositoryImpl
import org.change360.app.data.repository.ChallengeRepositoryImpl
import org.change360.app.data.repository.ImpactRepositoryImpl
import org.change360.app.data.repository.PosterRepositoryImpl
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

/**
 * Dependency Injection container interface declaring all singletons and shared dependencies
 * for the CHANGE360 application.
 *
 * Responsibilities:
 * - Exposes single source of truth Room database [Change360Database] and [UserPreferencesDataSource].
 * - Exposes domain repository contracts: [AuthRepository], [ChallengeRepository],
 *   [ImpactRepository], [AiAssistantRepository], and [PosterRepository].
 * - Exposes domain use cases: [ObserveAuthStateUseCase], [AuthenticateUserUseCase],
 *   [GetDashboardOverviewUseCase], [CompleteChallengeUseCase], [CalculateImpactStatsUseCase],
 *   [SendAiMessageUseCase], and [SaveGeneratedPosterUseCase].
 */
interface AppContainer {
    val database: Change360Database
    val userPreferencesDataSource: UserPreferencesDataSource

    // Repositories
    val authRepository: AuthRepository
    val challengeRepository: ChallengeRepository
    val impactRepository: ImpactRepository
    val aiAssistantRepository: AiAssistantRepository
    val posterRepository: PosterRepository

    // Domain Use Cases
    val observeAuthStateUseCase: ObserveAuthStateUseCase
    val authenticateUserUseCase: AuthenticateUserUseCase
    val getDashboardOverviewUseCase: GetDashboardOverviewUseCase
    val completeChallengeUseCase: CompleteChallengeUseCase
    val calculateImpactStatsUseCase: CalculateImpactStatsUseCase
    val sendAiMessageUseCase: SendAiMessageUseCase
    val saveGeneratedPosterUseCase: SaveGeneratedPosterUseCase
}

/**
 * Default implementation of [AppContainer] managing lazy-initialized, singleton instances
 * of local databases, data stores, repositories, and domain use cases.
 *
 * All dependencies are instantiated lazily on first access, ensuring efficient startup
 * while guaranteeing single-instance semantics across the application lifecycle.
 *
 * @param context The [Context] used to construct Room and DataStore instances.
 * @param ioDispatcher The [CoroutineDispatcher] injected into repositories for background I/O operations.
 */
class DefaultAppContainer(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppContainer {

    private val applicationContext: Context = context.applicationContext

    override val database: Change360Database by lazy {
        Change360Database.getInstance(applicationContext)
    }

    override val userPreferencesDataSource: UserPreferencesDataSource by lazy {
        UserPreferencesDataSource(applicationContext.change360DataStore)
    }

    // Repositories
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            userDao = database.userDao(),
            preferencesDataSource = userPreferencesDataSource,
            ioDispatcher = ioDispatcher
        )
    }

    override val challengeRepository: ChallengeRepository by lazy {
        ChallengeRepositoryImpl(
            challengeDao = database.challengeDao(),
            impactLogDao = database.impactLogDao(),
            userDao = database.userDao(),
            preferencesDataSource = userPreferencesDataSource,
            ioDispatcher = ioDispatcher
        )
    }

    override val impactRepository: ImpactRepository by lazy {
        ImpactRepositoryImpl(
            impactLogDao = database.impactLogDao(),
            preferencesDataSource = userPreferencesDataSource,
            ioDispatcher = ioDispatcher
        )
    }

    override val aiAssistantRepository: AiAssistantRepository by lazy {
        AiAssistantRepositoryImpl(
            chatMessageDao = database.chatMessageDao(),
            challengeDao = database.challengeDao(),
            impactLogDao = database.impactLogDao(),
            ioDispatcher = ioDispatcher
        )
    }

    override val posterRepository: PosterRepository by lazy {
        PosterRepositoryImpl(
            posterDao = database.posterDao(),
            ioDispatcher = ioDispatcher
        )
    }

    // Domain Use Cases
    override val observeAuthStateUseCase: ObserveAuthStateUseCase by lazy {
        ObserveAuthStateUseCase(authRepository = authRepository)
    }

    override val authenticateUserUseCase: AuthenticateUserUseCase by lazy {
        AuthenticateUserUseCase(authRepository = authRepository)
    }

    override val getDashboardOverviewUseCase: GetDashboardOverviewUseCase by lazy {
        GetDashboardOverviewUseCase(
            authRepository = authRepository,
            challengeRepository = challengeRepository,
            impactRepository = impactRepository
        )
    }

    override val completeChallengeUseCase: CompleteChallengeUseCase by lazy {
        CompleteChallengeUseCase(challengeRepository = challengeRepository)
    }

    override val calculateImpactStatsUseCase: CalculateImpactStatsUseCase by lazy {
        CalculateImpactStatsUseCase(impactRepository = impactRepository)
    }

    override val sendAiMessageUseCase: SendAiMessageUseCase by lazy {
        SendAiMessageUseCase(aiAssistantRepository = aiAssistantRepository)
    }

    override val saveGeneratedPosterUseCase: SaveGeneratedPosterUseCase by lazy {
        SaveGeneratedPosterUseCase(posterRepository = posterRepository)
    }
}

