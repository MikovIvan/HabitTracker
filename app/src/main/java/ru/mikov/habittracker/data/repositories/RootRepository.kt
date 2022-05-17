package ru.mikov.habittracker.data.repositories

import androidx.lifecycle.LiveData
import ru.mikov.habittracker.data.local.DbManager.db
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitDone
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.local.entities.HabitUID
import ru.mikov.habittracker.data.remote.NetworkManager
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.remote.res.HabitRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes
import ru.mikov.habittracker.data.toHabitRes

object RootRepository {
    private var habitsDao = db.habitsDao()
    private var habitsUIDDao = db.habitsUIDDao()
    private var habitDoneDao = db.habitDoneDao()
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

    suspend fun addHabitUID(habitUID: HabitUID) {
        habitsUIDDao.insert(habitUID)
    }

    fun getHabit(id: String): LiveData<Habit?> {
        return habitsDao.findHabitById(id)
    }

    fun getHabitsByType(type: HabitType): LiveData<List<Habit>> {
        return habitsDao.findHabitsByType(type)
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

    suspend fun isNoHabits(): Boolean {
        return habitsDao.findAllHabits().isEmpty()
    }

    suspend fun getUnSyncHabits(): List<Habit> {
        return habitsDao.findUnSyncHabits()
    }

    suspend fun getUIDToDelete(): List<HabitUID> {
        return habitsUIDDao.getAllUID()
    }

    suspend fun clearUID() {
        return habitsUIDDao.clear()
    }

    suspend fun doneHabit(habitDoneRes: HabitDoneRes) {
        network.completeHabit(habitDoneRes)
    }

    suspend fun getUnSyncDoneDates(): List<HabitDone> {
        return habitDoneDao.findAllHabitDone()
    }

    suspend fun addHabitDone(habitDone: HabitDone) {
        habitDoneDao.insert(habitDone)
    }

    suspend fun deleteDoneDate(habitDone: HabitDone) {
        habitDoneDao.delete(habitDone)
    }
}

