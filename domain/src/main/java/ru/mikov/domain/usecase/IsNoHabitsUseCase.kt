package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.repository.RootRepository

class IsNoHabitsUseCase(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun isNoHabits(): Boolean {
        return withContext(dispatcher) {
            repository.isNoHabits()
        }
    }
}