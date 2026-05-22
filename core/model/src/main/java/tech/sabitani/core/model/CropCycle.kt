package tech.sabitani.core.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class CropCycle(
    val id: Long = 0L,
    val plotId: Long,
    val commodity: String,
    val variety: String?,
    val startDate: LocalDate,
    val targetHarvestDate: LocalDate?,
    val actualHarvestDate: LocalDate?,
    val status: CycleStatus,
    val notes: String?,
    val createdAt: Instant,
)

enum class CycleStatus(
    val displayName: String,
) {
    PLANNING("Direncanakan"),
    ACTIVE("Berjalan"),
    HARVESTED("Sudah Panen"),
    CANCELLED("Dibatalkan"),
}
