package org.change360.app

import android.app.Application
import org.change360.app.di.AppContainer
import org.change360.app.di.DefaultAppContainer

/**
 * Base [Application] class for the CHANGE360 Android application.
 *
 * Responsibilities:
 * - Application-level initialization and lifecycle management.
 * - Root host for manual Dependency Injection, initializing and exposing the singleton [AppContainer].
 */
class Change360Application : Application() {

    /**
     * Application-scoped [AppContainer] instance providing manual
     * dependency injection across Room database, DataStore, repositories, and use cases.
     */
    lateinit var container: AppContainer
        private set

    /**
     * Convenience property mirroring [container] for idiomatic dependency access.
     */
    val appContainer: AppContainer
        get() = container

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        container = DefaultAppContainer(applicationContext)
    }

    companion object {
        @Volatile
        private var INSTANCE: Change360Application? = null

        /**
         * Returns the global application singleton instance.
         */
        val instance: Change360Application
            get() = INSTANCE ?: error("Change360Application has not been initialized.")

        /**
         * Convenience accessor for the global [AppContainer] singleton.
         */
        val appContainer: AppContainer
            get() = instance.container
    }
}
