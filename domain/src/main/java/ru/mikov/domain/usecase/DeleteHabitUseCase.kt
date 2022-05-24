package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.models.HabitUID
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun deleteHabit(habitId: String, isConnected: Boolean, isSynchronized: Boolean) {
        withContext(dispatcher) {
            when {
                //если есть интернет
                isConnected -> {
                    repository.deleteHabitFromNetwork(habitId)
                    repository.deleteHabitFromDb(habitId)
                }
                //если нет интернета и привычка синхронизирована
                isSynchronized -> {
                    repository.addHabitUID(HabitUID(habitId))
                    repository.deleteHabitFromDb(habitId)
                }
                //если нет интернета и привычка несинхронизирована
                else -> {
                    repository.deleteHabitFromDb(habitId)
                }
            }
        }
    }
}