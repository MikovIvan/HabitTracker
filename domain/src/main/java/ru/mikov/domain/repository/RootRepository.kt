package ru.mikov.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.models.HabitUID

//лучше делить
interface RootRepository {
    suspend fun addHabitToDb(habit: Habit)

    suspend fun addHabitsToDb(habit: List<Habit>)

    suspend fun deleteHabitFromDb(habitId: String)

    suspend fun addHabitUID(habitUID: HabitUID)

    fun getHabit(id: String): Flow<Habit?>

    fun getHabitsByType(typeDomain: HabitTypeDomain): Flow<List<Habit>>

    suspend fun updateHabit(updatedHabit: Habit)

    suspend fun uploadHabitToNetwork(habit: Habit): Flow<HabitUID>

    suspend fun loadHabitsFromNetwork(): Flow<List<Habit>>

    suspend fun deleteHabitFromNetwork(habitUID: String): Flow<Unit>

    suspend fun isNoHabits(): Boolean

    suspend fun getUnSyncHabits(): List<Habit>

    suspend fun getUIDToDelete(): List<HabitUID>

    suspend fun clearUID()

    suspend fun doneHabit(habitDoneRes: HabitDone): Flow<Unit>

    suspend fun getUnSyncDoneDates(): List<HabitDone>

    suspend fun addHabitDone(habitDone: HabitDone)

    suspend fun deleteDoneDate(habitDone: HabitDone)
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}