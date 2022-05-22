package ru.mikov.habittracker.di

import dagger.Module
import dagger.Provides
import ru.mikov.data.remote.RestService
import ru.mikov.data.repository.RootRepositoryImpl
import ru.mikov.domain.repository.RootRepository
import ru.mikov.habittracker.data.local.dao.HabitDoneDao
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao

@Module
class DataModule {

    @Provides
    fun provideRootRepository(
        habitDoneDao: HabitDoneDao,
        habitsDao: HabitsDao,
        habitsUIDDao: HabitsUIDDao,
        api: RestService
    ): RootRepository {
        return RootRepositoryImpl(
            habitDoneDao = habitDoneDao,
            habitsDao = habitsDao,
            habitsUIDDao = habitsUIDDao,
            api = api
        )
    }
}