package tech.sabitani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plots",
    foreignKeys = [
        ForeignKey(
            entity = FarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["farmId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("farmId")],
)
data class PlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val farmId: Long,
    val name: String,
    val areaSqM: Double,
    val soilType: String,
    val irrigationType: String,
    val notes: String?,
    val createdAtEpochMillis: Long,
)
