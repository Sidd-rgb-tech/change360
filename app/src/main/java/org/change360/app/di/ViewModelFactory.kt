package org.change360.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import org.change360.app.Change360Application
import org.change360.app.data.local.Change360Database
import org.change360.app.data.local.datastore.UserPreferencesDataSource
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
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * Universal [ViewModelProvider.Factory] responsible for instantiating ViewModels
 * in the CHANGE360 architecture using dependencies wired through [AppContainer].
 *
 * Supports:
 * 1. Registered custom factory creators via [creators] map or [ViewModelFactoryBuilder].
 * 2. Automatic dependency resolution by matching constructor parameter types against
 *    dependencies exposed in [AppContainer] (UseCases, Repositories, Database, DataStore).
 * 3. Modern Jetpack Compose [CreationExtras] interoperability.
 *
 * @param appContainer The root dependency container providing singleton services.
 * @param creators Optional registry mapping specific ViewModel classes to instantiation lambdas.
 */
open class ViewModelFactory(
    private val appContainer: AppContainer,
    private val creators: Map<Class<out ViewModel>, (AppContainer) -> ViewModel> = emptyMap()
) : ViewModelProvider.Factory {

    /**
     * Pool of injectable dependencies sourced from the [AppContainer] used
     * to fulfill ViewModel constructor parameters via reflection.
     */
    private val availableDependencies: List<Any> by lazy {
        listOfNotNull(
            appContainer,
            runCatching { appContainer.database }.getOrNull(),
            runCatching { appContainer.userPreferencesDataSource }.getOrNull(),

            // Repositories
            runCatching { appContainer.authRepository }.getOrNull(),
            runCatching { appContainer.challengeRepository }.getOrNull(),
            runCatching { appContainer.impactRepository }.getOrNull(),
            runCatching { appContainer.aiAssistantRepository }.getOrNull(),
            runCatching { appContainer.posterRepository }.getOrNull(),

            // Domain Use Cases
            runCatching { appContainer.observeAuthStateUseCase }.getOrNull(),
            runCatching { appContainer.authenticateUserUseCase }.getOrNull(),
            runCatching { appContainer.getDashboardOverviewUseCase }.getOrNull(),
            runCatching { appContainer.completeChallengeUseCase }.getOrNull(),
            runCatching { appContainer.calculateImpactStatsUseCase }.getOrNull(),
            runCatching { appContainer.sendAiMessageUseCase }.getOrNull(),
            runCatching { appContainer.saveGeneratedPosterUseCase }.getOrNull()
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return create(modelClass)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Direct or assignable match in custom creators map
        val explicitCreator = creators[modelClass]
            ?: creators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value

        if (explicitCreator != null) {
            val instance = explicitCreator(appContainer)
            return modelClass.cast(instance) as T
        }

        // 2. Automated constructor injection via available container dependencies
        val instance = instantiateViaReflection(modelClass)
        if (instance != null) {
            return instance
        }

        throw IllegalArgumentException(
            "Cannot construct ViewModel instance for ${modelClass.name}. " +
                    "Ensure its constructor parameters match dependencies provided by AppContainer, " +
                    "or register an explicit creator in ViewModelFactory."
        )
    }

    /**
     * Attempts to find and invoke a public constructor on [modelClass] whose parameter
     * types can all be satisfied by [availableDependencies].
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : ViewModel> instantiateViaReflection(modelClass: Class<T>): T? {
        val constructors = modelClass.constructors
            .sortedByDescending { it.parameterTypes.size }

        for (constructor in constructors) {
            val paramTypes = constructor.parameterTypes
            val args = arrayOfNulls<Any>(paramTypes.size)
            var canSatisfyAll = true

            for (i in paramTypes.indices) {
                val paramType = paramTypes[i]
                val matchedDep = availableDependencies.firstOrNull { dep ->
                    paramType.isAssignableFrom(dep.javaClass)
                }

                if (matchedDep != null) {
                    args[i] = matchedDep
                } else {
                    canSatisfyAll = false
                    break
                }
            }

            if (canSatisfyAll) {
                return try {
                    constructor.isAccessible = true
                    constructor.newInstance(*args) as T
                } catch (e: InvocationTargetException) {
                    throw (e.targetException ?: e)
                }
            }
        }

        return null
    }

    companion object {
        /**
         * Convenience factory creating a [ViewModelFactory] backed by the singleton [AppContainer]
         * stored in [Change360Application].
         */
        fun createDefault(): ViewModelFactory =
            ViewModelFactory(Change360Application.appContainer)

        /**
         * Starts building a [ViewModelFactory] with custom creator mappings.
         */
        fun builder(appContainer: AppContainer): ViewModelFactoryBuilder =
            ViewModelFactoryBuilder(appContainer)
    }
}

/**
 * Typealias preserving the project naming scheme referenced across UI modules.
 */
typealias Change360ViewModelFactory = ViewModelFactory

/**
 * Fluent builder for configuring custom ViewModel creator functions.
 */
class ViewModelFactoryBuilder(private val appContainer: AppContainer) {
    @PublishedApi
    internal val creators = mutableMapOf<Class<out ViewModel>, (AppContainer) -> ViewModel>()

    /**
     * Registers a creator lambda for the specified ViewModel class [VM].
     */
    inline fun <reified VM : ViewModel> addCreator(
        noinline creator: (AppContainer) -> VM
    ): ViewModelFactoryBuilder {
        creators[VM::class.java] = creator
        return this
    }

    /**
     * Constructs the configured [ViewModelFactory].
     */
    fun build(): ViewModelFactory = ViewModelFactory(appContainer, creators)
}

/**
 * Creates a lightweight [ViewModelProvider.Factory] dedicated to a single [VM] type.
 */
inline fun <reified VM : ViewModel> provideViewModelFactory(
    appContainer: AppContainer,
    crossinline creator: (AppContainer) -> VM
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return create(modelClass)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VM::class.java)) {
            return creator(appContainer) as T
        }
        throw IllegalArgumentException(
            "Expected ViewModel of type ${VM::class.java.name} but requested ${modelClass.name}"
        )
    }
}

/**
 * Extension property on [CreationExtras] to extract the host [Change360Application].
 */
val CreationExtras.change360Application: Change360Application
    get() = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? Change360Application)
        ?: error("CreationExtras does not contain an instance of Change360Application.")

/**
 * Extension property on [CreationExtras] to access the singleton [AppContainer].
 */
val CreationExtras.appContainer: AppContainer
    get() = change360Application.container

/**
 * Convenience extension on [AppContainer] to create a [ViewModelFactory] with optional creator pairs.
 */
fun AppContainer.createViewModelFactory(
    vararg pairs: Pair<Class<out ViewModel>, (AppContainer) -> ViewModel>
): ViewModelFactory = ViewModelFactory(this, pairs.toMap())

