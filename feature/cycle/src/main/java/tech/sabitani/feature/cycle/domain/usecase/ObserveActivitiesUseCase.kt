package tech.sabitani.feature.cycle.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.FarmActivity
import tech.sabitani.feature.cycle.domain.repository.FarmActivityRepository

class ObserveActivitiesUseCase @Inject constructor(
    private val activityRepository: FarmActivityRepository,
) {
    operator fun invoke(cycleId: Long): Flow<List<FarmActivity>> =
        activityRepository.observeActivities(cycleId)
}
