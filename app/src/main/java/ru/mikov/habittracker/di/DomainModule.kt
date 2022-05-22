package ru.mikov.habittracker.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.mikov.domain.repository.RootRepository
import ru.mikov.domain.usecase.*

@Module
class DomainModule {

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    fun provideDeleteHabitUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): DeleteHabitUseCase {
        return DeleteHabitUseCase(repository, dispatcher)
    }

    @Provides
    fun provideGetHabitsUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): GetHabitsUseCase {
        return GetHabitsUseCase(repository, dispatcher)
    }

    @Provides
    fun provideGetHabitUseCase(
        repository: RootRepository
    ): GetHabitUseCase {
        return GetHabitUseCase(repository)
    }

    @Provides
    fun provideIsNoHabitsUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): IsNoHabitsUseCase {
        return IsNoHabitsUseCase(repository, dispatcher)
    }

    @Provides
    fun provideLoadHabitsUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): LoadHabitsUseCase {
        return LoadHabitsUseCase(repository, dispatcher)
    }

    @Provides
    fun provideSaveHabitUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): SaveHabitUseCase {
        return SaveHabitUseCase(repository, dispatcher)
    }

    @Provides
    fun provideSyncDeletedHabitsWithServerUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): SyncDeletedHabitsWithServerUseCase {
        return SyncDeletedHabitsWithServerUseCase(repository, dispatcher)
    }

    @Provides
    fun provideUpdateHabitUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): UpdateHabitUseCase {
        return UpdateHabitUseCase(repository, dispatcher)
    }

    @Provides
    fun provideUploadUnSyncHabitsToServerUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): UploadUnSyncHabitsToServerUseCase {
        return UploadUnSyncHabitsToServerUseCase(repository, dispatcher)
    }

    @Provides
    fun provideDoneHabitUseCase(
        repository: RootRepository,
        dispatcher: CoroutineDispatcher
    ): DoneHabitUseCase {
        return DoneHabitUseCase(repository, dispatcher)
    }
}