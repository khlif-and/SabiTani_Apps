package tech.sabitani.core.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class FarmActivity(
    val id: Long = 0L,
    val cycleId: Long,
    val type: ActivityType,
    val performedOn: LocalDate,
    val material: String?,
    val dosage: String?,
    val notes: String?,
    val createdAt: Instant,
)

enum class ActivityType(
    val displayName: String,
) {
    PLANTING("Tanam"),
    FERTILIZING("Pemupukan"),
    PESTICIDE("Pestisida"),
    WATERING("Penyiraman"),
    WEEDING("Penyiangan"),
    REPLANTING("Sulam"),
    HARVESTING("Panen"),
    OBSERVATION("Pengamatan"),
    OTHER("Lainnya"),
}
