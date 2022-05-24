package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.models.Habit
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun update(habit: Habit, isConnected: Boolean) {
        withContext(dispatcher) {
            if (isConnected) {
                repository.uploadHabitToNetwork(habit)
                repository.updateHabit(habit.copy(isSynchronized = true))
            } else {
                repository.updateHabit(habit.copy(isSynchronized = false))
            }
        }
    }
}