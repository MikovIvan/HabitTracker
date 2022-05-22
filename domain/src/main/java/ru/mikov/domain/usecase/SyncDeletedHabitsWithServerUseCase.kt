package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.repository.RootRepository

class SyncDeletedHabitsWithServerUseCase(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun sync() {
        withContext(dispatcher) {
            val uids = repository.getUIDToDelete()
            uids.forEach {
                repository.deleteHabitFromNetwork(it.uid)
            }
            repository.clearUID()
        }
    }
}