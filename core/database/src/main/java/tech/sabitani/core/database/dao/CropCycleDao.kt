package tech.sabitani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.database.entity.CropCycleEntity

@Dao
interface CropCycleDao {
    @Query("SELECT * FROM crop_cycles WHERE plotId = :plotId ORDER BY startDateIso DESC")
    fun observeByPlot(plotId: Long): Flow<List<CropCycleEntity>>

    @Query("SELECT * FROM crop_cycles WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CropCycleEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: CropCycleEntity): Long

    @Update
    suspend fun update(entity: CropCycleEntity)

    @Query("DELETE FROM crop_cycles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
