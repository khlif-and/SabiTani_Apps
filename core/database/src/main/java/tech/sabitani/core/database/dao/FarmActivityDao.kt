package tech.sabitani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.database.entity.FarmActivityEntity

@Dao
interface FarmActivityDao {
    @Query("SELECT * FROM farm_activities WHERE cycleId = :cycleId ORDER BY performedOnIso DESC, id DESC")
    fun observeByCycle(cycleId: Long): Flow<List<FarmActivityEntity>>

    @Query("SELECT * FROM farm_activities WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<FarmActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: FarmActivityEntity): Long

    @Update
    suspend fun update(entity: FarmActivityEntity)

    @Query("DELETE FROM farm_activities WHERE id = :id")
    suspend fun deleteById(id: Long)
}
