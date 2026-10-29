package org.change360.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Convenience extension property providing access to the application-level preferences DataStore.
 */
val Context.change360DataStore: DataStore<Preferences> by preferencesDataStore(name = "change360_user_preferences")

/**
 * Immutable snapshot of persisted lightweight user session preferences.
 */
data class UserPreferencesData(
    val activeUserId: String? = null,
    val isAuthenticated: Boolean = false,
    val themeAuraVariant: String = UserPreferencesDataSource.DEFAULT_THEME_AURA_VARIANT,
    val lastActiveEpochDay: Long = 0L
)

/**
 * Data source managing lightweight key-value application session state backed by Jetpack DataStore.
 *
 * Persists session authorization flags, active user references, visual Aura themes,
 * and daily activity epoch timestamps for streak tracking.
 */
class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val KEY_ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
        val KEY_IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
        val KEY_THEME_AURA_VARIANT = stringPreferencesKey("theme_aura_variant")
        val KEY_LAST_ACTIVE_EPOCH_DAY = longPreferencesKey("last_active_epoch_day")
    }

    /**
     * Cold stream emitting full user preferences snapshots with automatic error recovery.
     */
    val userPreferences: Flow<UserPreferencesData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferencesData(
                activeUserId = preferences[PreferencesKeys.KEY_ACTIVE_USER_ID],
                isAuthenticated = preferences[PreferencesKeys.KEY_IS_AUTHENTICATED] ?: false,
                themeAuraVariant = preferences[PreferencesKeys.KEY_THEME_AURA_VARIANT] ?: DEFAULT_THEME_AURA_VARIANT,
                lastActiveEpochDay = preferences[PreferencesKeys.KEY_LAST_ACTIVE_EPOCH_DAY] ?: 0L
            )
        }

    /**
     * Emits the currently active authenticated user identifier, or null if unauthenticated.
     */
    val activeUserId: Flow<String?> = userPreferences.map { it.activeUserId }

    /**
     * Emits whether a valid user session is presently active.
     */
    val isAuthenticated: Flow<Boolean> = userPreferences.map { it.isAuthenticated }

    /**
     * Emits the selected Aura gradient theme identifier.
     */
    val themeAuraVariant: Flow<String> = userPreferences.map { it.themeAuraVariant }

    /**
     * Emits the last recorded activity epoch day for streak tracking.
     */
    val lastActiveEpochDay: Flow<Long> = userPreferences.map { it.lastActiveEpochDay }

    /**
     * Updates the stored active user identifier.
     */
    suspend fun setActiveUserId(userId: String?) {
        dataStore.edit { preferences ->
            if (userId != null) {
                preferences[PreferencesKeys.KEY_ACTIVE_USER_ID] = userId
            } else {
                preferences.remove(PreferencesKeys.KEY_ACTIVE_USER_ID)
            }
        }
    }

    /**
     * Updates the authentication status flag.
     */
    suspend fun setAuthenticated(isAuthenticated: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_IS_AUTHENTICATED] = isAuthenticated
        }
    }

    /**
     * Sets the active Aura color theme variant.
     */
    suspend fun setThemeAuraVariant(variant: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_THEME_AURA_VARIANT] = variant
        }
    }

    /**
     * Updates the last active epoch day.
     */
    suspend fun setLastActiveEpochDay(epochDay: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_LAST_ACTIVE_EPOCH_DAY] = epochDay
        }
    }

    /**
     * Saves an authenticated user session in a single transactional edit.
     */
    suspend fun saveSession(userId: String, isAuthenticated: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_ACTIVE_USER_ID] = userId
            preferences[PreferencesKeys.KEY_IS_AUTHENTICATED] = isAuthenticated
        }
    }

    /**
     * Clears all session credentials and resets authentication state.
     */
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.KEY_ACTIVE_USER_ID)
            preferences[PreferencesKeys.KEY_IS_AUTHENTICATED] = false
        }
    }

    companion object {
        const val DEFAULT_THEME_AURA_VARIANT: String = "BIO_EMERALD"
    }
}

