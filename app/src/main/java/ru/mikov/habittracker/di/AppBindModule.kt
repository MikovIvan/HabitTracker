package ru.mikov.habittracker.di

import dagger.Binds
import dagger.Module
import ru.mikov.data.repository.RootRepositoryImpl
import ru.mikov.domain.repository.RootRepository

@Module
interface AppBindModule {

    @Binds
    fun bindsRootRepositoryImplToRootRepository(
        rootRepositoryImpl: RootRepositoryImpl
    ): RootRepository
}