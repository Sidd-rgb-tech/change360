package org.change360.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.change360.app.data.local.entity.PosterEntity

/**
 * Data Access Object for generated advocacy posters and saved templates.
 */
@Dao
interface PosterDao {

    /**
     * Observes all saved advocacy posters ordered by creation timestamp descending.
     */
    @Query("SELECT * FROM posters ORDER BY createdAtEpochMillis DESC")
    fun observeSavedPosters(): Flow<List<PosterEntity>>

    /**
     * Observes an individual poster configuration by ID.
     */
    @Query("SELECT * FROM posters WHERE id = :id LIMIT 1")
    fun observePosterById(id: Long): Flow<PosterEntity?>

    /**
     * Retrieves an individual poster snapshot.
     */
    @Query("SELECT * FROM posters WHERE id = :id LIMIT 1")
    suspend fun getPosterById(id: Long): PosterEntity?

    /**
     * Inserts an advocacy poster, returning the generated row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoster(poster: PosterEntity): Long

    /**
     * Inserts a batch of poster records.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosters(posters: List<PosterEntity>)

    /**
     * Updates an existing poster configuration.
     */
    @Update
    suspend fun updatePoster(poster: PosterEntity)

    /**
     * Updates the exported image file URI string for a specific poster.
     */
    @Query("UPDATE posters SET exportedUriString = :exportedUriString WHERE id = :id")
    suspend fun updateExportedUri(id: Long, exportedUriString: String)

    /**
     * Deletes a poster by primary key.
     */
    @Query("DELETE FROM posters WHERE id = :id")
    suspend fun deletePosterById(id: Long)

    /**
     * Clears all saved posters.
     */
    @Query("DELETE FROM posters")
    suspend fun clearAllPosters()
}

