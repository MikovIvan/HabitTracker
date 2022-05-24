package ru.mikov.habittracker.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.mikov.habittracker.presentation.habit.HabitViewModel
import ru.mikov.habittracker.presentation.habits.HabitsViewModel
import ru.mikov.habittracker.presentation.main.MainActivity
import ru.mikov.habittracker.presentation.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppBindModule::class, DbModule::class, DomainModule::class, NetworkModule::class, DispatcherModule::class, AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun subComponent(): SubComponent.Factory

    fun mainViewModel(): MainViewModel.Factory
    fun habitViewModel(): HabitViewModel.Factory
    fun habitsViewModel(): HabitsViewModel.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}