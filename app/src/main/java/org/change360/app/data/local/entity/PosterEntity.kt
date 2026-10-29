package org.change360.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing an advocacy poster created or customized by the user.
 *
 * Stores aesthetic configuration parameters, personal eco-pledge typography,
 * and rendered image file URI references.
 */
@Entity(
    tableName = "posters",
    indices = [
        Index(value = ["createdAtEpochMillis"])
    ]
)
data class PosterEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "pledgeQuote")
    val pledgeQuote: String,

    @ColumnInfo(name = "templateStyle")
    val templateStyle: String,

    @ColumnInfo(name = "accentColorHex")
    val accentColorHex: String,

    @ColumnInfo(name = "exportedUriString")
    val exportedUriString: String? = null,

    @ColumnInfo(name = "createdAtEpochMillis")
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

