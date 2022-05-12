package ru.mikov.habittracker.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mikov.habittracker.App
import ru.mikov.habittracker.BuildConfig
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitUID

object DbManager {
    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()
}

@Database(
    entities = [Habit::class, HabitUID::class],
    version = AppDb.DATABASE_VERSION,
    exportSchema = false,
    views = []
)
@TypeConverters(HabitTypeConverter::class, HabitPriorityConverter::class)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun habitsDao(): HabitsDao
    abstract fun habitsUIDDao(): HabitsUIDDao
}