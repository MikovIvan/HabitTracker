package ru.mikov.habittracker.ui.habit

import androidx.lifecycle.ViewModel
import ru.mikov.habittracker.data.entities.Habit
import ru.mikov.habittracker.data.repositories.RootRepository

class HabitViewModel : ViewModel() {
    private val repository = RootRepository

    fun addHabit(habit: Habit) {
        repository.addHabit(habit)
    }

    fun update(habit: Habit) {
        repository.update(habit)
    }
}