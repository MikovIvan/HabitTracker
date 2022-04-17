package ru.mikov.habittracker.ui.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.repositories.RootRepository

class HabitViewModel : ViewModel() {
    private val repository = RootRepository

    fun addHabit(habit: Habit) {
        viewModelScope.launch { repository.addHabit(habit) }
    }

    fun update(habit: Habit) {
        viewModelScope.launch { repository.update(habit) }
    }

    fun getHabit(id: Int): Habit {
        return repository.getHabit(id)
    }
}