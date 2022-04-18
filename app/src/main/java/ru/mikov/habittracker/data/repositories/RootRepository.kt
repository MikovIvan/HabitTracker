package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.LiveData
import ru.mikov.habittracker.data.local.DbManager.db
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType

object RootRepository {
    private var habitsDao = db.habitsDao()

    suspend fun addHabit(habit: Habit) {
        habitsDao.insert(habit)
    }

    fun getHabit(id: Int): LiveData<Habit?> {
        return habitsDao.findHabitById(id)
    }

    fun getHabitsByType(type: HabitType): LiveData<List<Habit>> {
        return habitsDao.findHabits(type)
    }

    suspend fun update(updatedHabit: Habit) {
        habitsDao.update(updatedHabit)
    }
}

