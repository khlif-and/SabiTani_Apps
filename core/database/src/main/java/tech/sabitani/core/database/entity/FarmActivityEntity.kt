package tech.sabitani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "farm_activities",
    foreignKeys = [
        ForeignKey(
            entity = CropCycleEntity::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cycleId"), Index("performedOnIso")],
)
data class FarmActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cycleId: Long,
    val type: String,
    val performedOnIso: String,
    val material: String?,
    val dosage: String?,
    val notes: String?,
    val createdAtEpochMillis: Long,
)
