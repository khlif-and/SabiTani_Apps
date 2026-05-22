package tech.sabitani.core.data.dashboard

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import tech.sabitani.core.database.dao.CropCycleDao
import tech.sabitani.core.database.dao.FarmDao
import tech.sabitani.core.database.dao.PlotDao
import tech.sabitani.core.database.dao.TransactionDao
import tech.sabitani.core.model.CycleStatus
import tech.sabitani.core.model.DashboardSummary
import javax.inject.Inject

internal class DashboardRepositoryImpl
    @Inject
    constructor(
        private val farmDao: FarmDao,
        private val plotDao: PlotDao,
        private val cropCycleDao: CropCycleDao,
        private val transactionDao: TransactionDao,
        private val clock: Clock,
    ) : DashboardRepository {
        override fun observeSummary(): Flow<DashboardSummary> {
            val (startIso, endIso) = currentMonthIsoRange()
            return combine(
                farmDao.observeCount(),
                plotDao.observeTotalCount(),
                cropCycleDao.observeCountByStatus(CycleStatus.ACTIVE.name),
                transactionDao.observeTotalsForRange(startIso = startIso, endIso = endIso),
            ) { farmCount, plotCount, activeCycleCount, monthlyTotals ->
                DashboardSummary(
                    farmCount = farmCount,
                    plotCount = plotCount,
                    activeCycleCount = activeCycleCount,
                    monthlyIncomeIdr = monthlyTotals.totalIncomeIdr,
                    monthlyExpenseIdr = monthlyTotals.totalExpenseIdr,
                )
            }
        }

        private fun currentMonthIsoRange(): Pair<String, String> {
            val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val firstOfMonth = LocalDate(today.year, today.month, 1)
            val firstOfNextMonth =
                if (today.monthNumber == MONTHS_PER_YEAR) {
                    LocalDate(today.year + 1, 1, 1)
                } else {
                    LocalDate(today.year, today.monthNumber + 1, 1)
                }
            val lastOfMonth =
                LocalDate.fromEpochDays(firstOfNextMonth.toEpochDays() - 1)
            return firstOfMonth.toString() to lastOfMonth.toString()
        }

        private companion object {
            const val MONTHS_PER_YEAR = 12
        }
    }
