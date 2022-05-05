package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.LiveData
import ru.mikov.habittracker.data.local.DbManager.db
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.remote.NetworkManager
import ru.mikov.habittracker.data.remote.res.HabitRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes
import ru.mikov.habittracker.data.toHabitRes

object RootRepository {
    private var habitsDao = db.habitsDao()
    private val network = NetworkManager.api

    suspend fun addHabitToDb(habit: Habit) {
        habitsDao.insert(habit)
    }

    suspend fun addHabitsToDb(habit: List<Habit>) {
        habitsDao.insert(habit)
    }

    suspend fun deleteHabitFromDb(habitId: String) {
        habitsDao.deleteHabit(habitId)
    }

    fun getHabit(id: String): LiveData<Habit?> {
        return habitsDao.findHabitById(id)
    }

    fun getHabitsByType(type: HabitType): LiveData<List<Habit>> {
        return habitsDao.findHabits(type)
    }

    suspend fun updateHabit(updatedHabit: Habit) {
        habitsDao.update(updatedHabit)
    }

    suspend fun uploadHabitToNetwork(habit: Habit): HabitUIDRes {
        return network.addHabit(habit.toHabitRes())
    }

    suspend fun loadHabitsFromNetwork(): List<HabitRes> {
        return network.habits()
    }

    suspend fun deleteHabitFromNetwork(habitUID: String) {
        network.deleteHabit(HabitUIDRes(habitUID))
    }
}

