package ru.mikov.habittracker.ui.habits

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mikov.habittracker.data.entities.Habit
import ru.mikov.habittracker.data.repositories.RootRepository

class HabitsViewModel : ViewModel() {
    private val repository = RootRepository

    fun getHabits(): MutableLiveData<List<Habit>> {
        return repository.habits
    }
}