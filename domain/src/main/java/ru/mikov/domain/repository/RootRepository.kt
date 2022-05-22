package ru.mikov.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.models.HabitUID

interface RootRepository {
    suspend fun addHabitToDb(habit: Habit)

    suspend fun addHabitsToDb(habit: List<Habit>)

    suspend fun deleteHabitFromDb(habitId: String)

    suspend fun addHabitUID(habitUID: HabitUID)

    fun getHabit(id: String): Flow<Habit?>

    fun getHabitsByType(typeDomain: HabitTypeDomain): Flow<List<Habit>>

    suspend fun updateHabit(updatedHabit: Habit)

    suspend fun uploadHabitToNetwork(habit: Habit): HabitUID

    suspend fun loadHabitsFromNetwork(): List<Habit>

    suspend fun deleteHabitFromNetwork(habitUID: String)

    suspend fun isNoHabits(): Boolean

    suspend fun getUnSyncHabits(): List<Habit>

    suspend fun getUIDToDelete(): List<HabitUID>

    suspend fun clearUID()

    suspend fun doneHabit(habitDoneRes: HabitDone)

    suspend fun getUnSyncDoneDates(): List<HabitDone>

    suspend fun addHabitDone(habitDone: HabitDone)

    suspend fun deleteDoneDate(habitDone: HabitDone)
}