package org.change360.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.change360.app.data.local.dao.ChallengeDao
import org.change360.app.data.local.dao.ChatMessageDao
import org.change360.app.data.local.dao.ImpactLogDao
import org.change360.app.data.local.dao.PosterDao
import org.change360.app.data.local.dao.UserDao
import org.change360.app.data.local.entity.ChallengeEntity
import org.change360.app.data.local.entity.ChatMessageEntity
import org.change360.app.data.local.entity.ImpactLogEntity
import org.change360.app.data.local.entity.PosterEntity
import org.change360.app.data.local.entity.UserEntity

/**
 * Primary Room database for CHANGE360.
 *
 * Serves as the offline-first Single Source of Truth (SSOT) maintaining
 * user progression, active sustainability challenges, verified impact logs,
 * conversational AI coach dialogue history, and advocacy poster configurations.
 */
@Database(
    entities = [
        UserEntity::class,
        ChallengeEntity::class,
        ImpactLogEntity::class,
        ChatMessageEntity::class,
        PosterEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class Change360Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun impactLogDao(): ImpactLogDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun posterDao(): PosterDao

    companion object {
        const val DATABASE_NAME: String = "change360.db"

        @Volatile
        private var INSTANCE: Change360Database? = null

        /**
         * Returns the thread-safe singleton instance of [Change360Database].
         */
        fun getInstance(
            context: Context,
            callback: RoomDatabase.Callback = SeedDatabaseCallback()
        ): Change360Database {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext, callback).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            callback: RoomDatabase.Callback
        ): Change360Database {
            return Room.databaseBuilder(
                context,
                Change360Database::class.java,
                DATABASE_NAME
            )
                .addCallback(callback)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

