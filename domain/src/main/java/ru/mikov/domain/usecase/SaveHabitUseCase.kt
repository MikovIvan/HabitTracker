package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.models.Habit
import ru.mikov.domain.repository.RootRepository
import java.util.*

class SaveHabitUseCase(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun save(habit: Habit, isConnected: Boolean) {
        withContext(dispatcher) {
            if (isConnected) {
                val id = repository.uploadHabitToNetwork(habit).uid
                repository.addHabitToDb(habit.copy(uid = id))
            } else {
                repository.addHabitToDb(
                    habit.copy(
                        uid = UUID.randomUUID().toString(),
                        isSynchronized = false
                    )
                )
            }
        }
    }
}