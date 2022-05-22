package ru.mikov.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mikov.data.BuildConfig
import ru.mikov.data.local.entities.HabitEntity
import ru.mikov.habittracker.data.local.DoneDatesConverter
import ru.mikov.habittracker.data.local.HabitPriorityConverter
import ru.mikov.habittracker.data.local.HabitTypeConverter
import ru.mikov.habittracker.data.local.dao.HabitDoneDao
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao
import ru.mikov.habittracker.data.local.entities.HabitDoneEntity
import ru.mikov.habittracker.data.local.entities.HabitUIDEntity

@Database(
    entities = [HabitEntity::class, HabitUIDEntity::class, HabitDoneEntity::class],
    version = AppDb.DATABASE_VERSION,
    exportSchema = false,
    views = []
)
@TypeConverters(HabitTypeConverter::class, HabitPriorityConverter::class, DoneDatesConverter::class)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun habitsDao(): HabitsDao
    abstract fun habitsUIDDao(): HabitsUIDDao
    abstract fun habitDoneDao(): HabitDoneDao
}