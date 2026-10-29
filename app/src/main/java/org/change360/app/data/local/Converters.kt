package org.change360.app.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.Date

/**
 * Room TypeConverters for mapping complex and temporal types to SQLite primitive representations.
 */
class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    /**
     * Converts an epoch millisecond timestamp to a java.time.Instant.
     */
    @TypeConverter
    fun fromEpochMilliToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    /**
     * Converts a java.time.Instant to an epoch millisecond timestamp.
     */
    @TypeConverter
    fun fromInstantToEpochMilli(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    /**
     * Converts a millisecond timestamp to a java.util.Date.
     */
    @TypeConverter
    fun fromTimestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a java.util.Date to a millisecond timestamp.
     */
    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Serializes a list of strings into a JSON string for SQLite storage.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        return json.encodeToString(list)
    }

    /**
     * Deserializes a JSON string back into a list of strings.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (_: Exception) {
            value.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    }
}

