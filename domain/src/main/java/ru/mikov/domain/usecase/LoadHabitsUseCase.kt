package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class LoadHabitsUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun loadHabits() {
        withContext(dispatcher) {
            val habits = repository.loadHabitsFromNetwork()
            repository.addHabitsToDb(habits)
        }
    }
}