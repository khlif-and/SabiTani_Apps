package tech.sabitani.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.sabitani.core.database.converter.DateConverters
import tech.sabitani.core.database.dao.CropCycleDao
import tech.sabitani.core.database.dao.FarmActivityDao
import tech.sabitani.core.database.dao.FarmDao
import tech.sabitani.core.database.dao.PlotDao
import tech.sabitani.core.database.dao.TransactionDao
import tech.sabitani.core.database.entity.CropCycleEntity
import tech.sabitani.core.database.entity.FarmActivityEntity
import tech.sabitani.core.database.entity.FarmEntity
import tech.sabitani.core.database.entity.PlotEntity
import tech.sabitani.core.database.entity.TransactionEntity

@Database(
    entities = [
        FarmEntity::class,
        PlotEntity::class,
        CropCycleEntity::class,
        FarmActivityEntity::class,
        TransactionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DateConverters::class)
abstract class SabiTaniDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun plotDao(): PlotDao
    abstract fun cropCycleDao(): CropCycleDao
    abstract fun farmActivityDao(): FarmActivityDao
    abstract fun transactionDao(): TransactionDao
}
