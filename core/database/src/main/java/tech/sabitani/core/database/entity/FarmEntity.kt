package tech.sabitani.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val location: String?,
    val totalAreaSqM: Double?,
    val createdAtEpochMillis: Long,
)
