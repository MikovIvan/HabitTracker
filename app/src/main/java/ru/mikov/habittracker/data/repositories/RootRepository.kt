package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.MutableLiveData
import ru.mikov.habittracker.data.entities.Habit
import ru.mikov.habittracker.ui.habits.State

object RootRepository {
    val habits = MutableLiveData<List<Habit>>()
    private val filteredHabits = MutableLiveData<List<Habit>>()

    fun addHabit(habit: Habit) {
        val copy = habits.value.orEmpty().toMutableList()
        copy.add(habit)
        habits.postValue(copy)
    }

    fun update(updatedHabit: Habit) {
        val copy = habits.value.orEmpty().toMutableList()

        copy.forEachIndexed { index, habit ->
            val changedHabit = copy.find { habit.id == updatedHabit.id }
            if (changedHabit != null) {
                val replacement = habit.copy(
                    id = updatedHabit.id,
                    name = updatedHabit.name,
                    description = updatedHabit.description,
                    priority = updatedHabit.priority,
                    type = updatedHabit.type,
                    periodicity = updatedHabit.periodicity,
                    numberOfExecutions = updatedHabit.numberOfExecutions,
                    color = updatedHabit.color
                )
                copy[index] = replacement
            }
        }

        habits.postValue(copy)
    }

    fun getData(state: State): MutableLiveData<List<Habit>> {
        val copy = habits.value.orEmpty().toMutableList()
        val filteredList = copy.filter { habit -> habit.type == state.type }
        var result = filteredList

        if (state.typeOfSort == 0 && state.sortBy) {
            result = filteredList.sortedBy { it.name }
        } else if (state.typeOfSort == 0 && !state.sortBy) {
            result = filteredList.sortedByDescending { it.name }
        } else if (state.typeOfSort == 1 && state.sortBy) {
            result = filteredList.sortedBy { it.periodicity }
        } else if (state.typeOfSort == 1 && !state.sortBy) {
            result = filteredList.sortedByDescending { it.periodicity }
        }
        if (state.searchQuery.isNotBlank()) {
            result = filteredList.filter { it.name.startsWith(state.searchQuery.uppercase()) }
        }
        filteredHabits.postValue(result)
        return filteredHabits
    }


}