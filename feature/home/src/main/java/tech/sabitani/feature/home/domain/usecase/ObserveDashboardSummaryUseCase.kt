package tech.sabitani.feature.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.data.dashboard.DashboardRepository
import tech.sabitani.core.model.DashboardSummary
import javax.inject.Inject

class ObserveDashboardSummaryUseCase
    @Inject
    constructor(
        private val repository: DashboardRepository,
    ) {
        operator fun invoke(): Flow<DashboardSummary> = repository.observeSummary()
    }
