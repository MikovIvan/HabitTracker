package ru.mikov.habittracker.di

import dagger.Subcomponent
import ru.mikov.habittracker.presentation.dialogs.FilterDialog
import ru.mikov.habittracker.presentation.habit.HabitFragment
import ru.mikov.habittracker.presentation.habits.HabitsFragment

@Subcomponent
interface SubComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SubComponent
    }

    fun inject(habitFragment: HabitFragment)
    fun inject(habitsFragment: HabitsFragment)
    fun inject(filterDialog: FilterDialog)
}