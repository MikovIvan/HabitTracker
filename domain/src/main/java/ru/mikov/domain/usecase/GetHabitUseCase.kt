package ru.mikov.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.mikov.domain.models.Habit
import ru.mikov.domain.repository.RootRepository

class GetHabitUseCase(
    private val repository: RootRepository
) {
    fun getHabit(uid: String): Flow<Habit?> {
        return repository.getHabit(uid)
    }
}