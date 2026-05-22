package tech.sabitani.feature.cycle.domain.usecase

import javax.inject.Inject
import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.ActivityType
import tech.sabitani.feature.cycle.domain.repository.FarmActivityRepository

class AddActivityUseCase @Inject constructor(
    private val activityRepository: FarmActivityRepository,
) {
    suspend operator fun invoke(
        cycleId: Long,
        type: ActivityType,
        performedOn: LocalDate,
        material: String?,
        dosage: String?,
        notes: String?,
    ): Result<Long> = runCatching {
        require(cycleId > 0L) { "Aktivitas harus terkait siklus tanam." }
        activityRepository.addActivity(
            cycleId = cycleId,
            type = type,
            performedOn = performedOn,
            material = material?.trim()?.takeIf(String::isNotEmpty),
            dosage = dosage?.trim()?.takeIf(String::isNotEmpty),
            notes = notes?.trim()?.takeIf(String::isNotEmpty),
        )
    }
}
