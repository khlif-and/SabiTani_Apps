package tech.sabitani.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CropCycleEntity::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("cycleId"), Index("type"), Index("occurredOnIso")],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cycleId: Long,
    val type: String,
    val category: String,
    val amountIdr: Long,
    val occurredOnIso: String,
    val notes: String?,
    val createdAtEpochMillis: Long,
)
