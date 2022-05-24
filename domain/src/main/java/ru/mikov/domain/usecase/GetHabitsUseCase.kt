package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.repository.RootRepository
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    fun getHabits(typeDomain: HabitTypeDomain): Flow<List<Habit>> {
        return repository.getHabitsByType(typeDomain).flowOn(dispatcher)
    }
}