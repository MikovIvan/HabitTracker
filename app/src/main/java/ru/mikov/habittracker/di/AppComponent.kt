package ru.mikov.habittracker.di

import dagger.Component
import ru.mikov.habittracker.presentation.habit.HabitFragment
import ru.mikov.habittracker.presentation.habit.HabitViewModel
import ru.mikov.habittracker.presentation.habits.HabitsFragment
import ru.mikov.habittracker.presentation.habits.HabitsViewModel
import ru.mikov.habittracker.presentation.main.MainActivity
import ru.mikov.habittracker.presentation.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DbModule::class, DomainModule::class, NetworkModule::class, DataModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(habitFragment: HabitFragment)
    fun inject(habitsFragment: HabitsFragment)


    fun mainViewModel(): MainViewModel.Factory
    fun habitViewModel(): HabitViewModel.Factory
    fun habitsViewModel(): HabitsViewModel.Factory
}