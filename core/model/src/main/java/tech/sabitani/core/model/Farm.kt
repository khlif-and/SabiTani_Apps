package tech.sabitani.core.model

import kotlinx.datetime.Instant

data class Farm(
    val id: Long = 0L,
    val name: String,
    val location: String?,
    val totalAreaSqM: Double?,
    val createdAt: Instant,
)

enum class SoilType(val displayName: String) {
    CLAY("Liat"),
    LOAM("Lempung"),
    SANDY("Pasir"),
    PEAT("Gambut"),
    OTHER("Lainnya"),
}

enum class IrrigationType(val displayName: String) {
    RAIN_FED("Tadah Hujan"),
    SURFACE("Irigasi Permukaan"),
    DRIP("Tetes"),
    SPRINKLER("Sprinkler"),
    OTHER("Lainnya"),
}
