package ru.mikov.habittracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.mikov.data.local.AppDb
import ru.mikov.data.local.PrefManager
import ru.mikov.habittracker.data.local.dao.HabitDoneDao
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    fun provideAppDb(context: Context): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideHabitDoneDao(db: AppDb): HabitDoneDao = db.habitDoneDao()

    @Provides
    @Singleton
    fun provideHabitsDao(db: AppDb): HabitsDao = db.habitsDao()

    @Provides
    @Singleton
    fun provideHabitUIDDao(db: AppDb): HabitsUIDDao = db.habitsUIDDao()

    @Provides
    @Singleton
    fun providePrefManger(context: Context): PrefManager = PrefManager(context)
}