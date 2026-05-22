package tech.sabitani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "crop_cycles",
    foreignKeys = [
        ForeignKey(
            entity = PlotEntity::class,
            parentColumns = ["id"],
            childColumns = ["plotId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plotId"), Index("status")],
)
data class CropCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val plotId: Long,
    val commodity: String,
    val variety: String?,
    val startDateIso: String,
    val targetHarvestDateIso: String?,
    val actualHarvestDateIso: String?,
    val status: String,
    val notes: String?,
    val createdAtEpochMillis: Long,
)
