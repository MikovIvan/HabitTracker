package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class IsNoHabitsUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun isNoHabits(): Boolean {
        return withContext(dispatcher) {
            repository.isNoHabits()
        }
    }
}