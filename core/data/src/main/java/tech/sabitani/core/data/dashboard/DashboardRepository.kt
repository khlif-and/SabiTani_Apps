package tech.sabitani.core.data.dashboard

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.DashboardSummary

interface DashboardRepository {
    fun observeSummary(): Flow<DashboardSummary>
}
