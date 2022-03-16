package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.MutableLiveData
import ru.mikov.habittracker.data.entities.Habit

object RootRepository {
    val habits = MutableLiveData<List<Habit>>()

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

}