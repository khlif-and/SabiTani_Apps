package tech.sabitani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.database.entity.PlotEntity

@Dao
interface PlotDao {
    @Query("SELECT * FROM plots WHERE farmId = :farmId ORDER BY createdAtEpochMillis DESC")
    fun observeByFarm(farmId: Long): Flow<List<PlotEntity>>

    @Query("SELECT * FROM plots WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PlotEntity?>

    @Query("SELECT COUNT(*) FROM plots")
    fun observeTotalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: PlotEntity): Long

    @Update
    suspend fun update(entity: PlotEntity)

    @Query("DELETE FROM plots WHERE id = :id")
    suspend fun deleteById(id: Long)
}
