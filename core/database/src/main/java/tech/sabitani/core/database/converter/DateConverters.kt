package tech.sabitani.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

internal class DateConverters {

    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun localDateToIsoString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun isoStringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)
}
