package tech.sabitani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.database.entity.TransactionEntity
import tech.sabitani.core.database.projection.CycleTransactionTotals
import tech.sabitani.core.database.projection.TransactionRangeTotals

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE cycleId = :cycleId ORDER BY occurredOnIso DESC, id DESC")
    fun observeByCycle(cycleId: Long): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT
            :cycleId AS cycleId,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amountIdr ELSE 0 END), 0) AS totalIncomeIdr,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amountIdr ELSE 0 END), 0) AS totalExpenseIdr,
            COUNT(*) AS transactionCount
        FROM transactions
        WHERE cycleId = :cycleId
        """,
    )
    fun observeTotalsByCycle(cycleId: Long): Flow<CycleTransactionTotals>

    @Query(
        """
        SELECT
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amountIdr ELSE 0 END), 0) AS totalIncomeIdr,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amountIdr ELSE 0 END), 0) AS totalExpenseIdr
        FROM transactions
        WHERE occurredOnIso >= :startIso AND occurredOnIso <= :endIso
        """,
    )
    fun observeTotalsForRange(
        startIso: String,
        endIso: String,
    ): Flow<TransactionRangeTotals>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: TransactionEntity): Long

    @Update
    suspend fun update(entity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
