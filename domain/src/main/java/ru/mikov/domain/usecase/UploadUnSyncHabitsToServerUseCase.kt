package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class UploadUnSyncHabitsToServerUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun upload() {
        withContext(dispatcher) {
            val unSyncHabits = repository.getUnSyncHabits()
            unSyncHabits.forEach { habit ->
                syncHabit(habit)
            }
            val unSyncDoneDates = repository.getUnSyncDoneDates()
            unSyncDoneDates.forEach { habitDone ->
                syncDoneDates(habitDone)
            }
        }
    }

    private suspend fun syncHabit(habit: Habit) {
        val id = repository.uploadHabitToNetwork(habit.copy(uid = "")).uid
        repository.deleteHabitFromDb(habit.uid)
        repository.addHabitToDb(habit.copy(uid = id, isSynchronized = true))
    }

    private suspend fun syncDoneDates(habitDone: HabitDone) {
        repository.doneHabit(habitDone)
        repository.deleteDoneDate(habitDone)
    }
}