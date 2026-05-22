package tech.sabitani.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.database.entity.FarmEntity

@Dao
interface FarmDao {

    @Query("SELECT * FROM farms ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<FarmEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: FarmEntity): Long

    @Update
    suspend fun update(entity: FarmEntity)

    @Query("DELETE FROM farms WHERE id = :id")
    suspend fun deleteById(id: Long)
}
