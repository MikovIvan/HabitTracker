package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.repository.RootRepository
import java.util.*

class DoneHabitUseCase(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun doneHabit(habit: Habit, isConnected: Boolean) {
        withContext(dispatcher) {
            val date = (Date().time / 1000).toInt()
            if (isConnected) {
                repository.updateHabit(
                    habit.copy(
                        doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                )
                repository.doneHabit(HabitDone(date, habit.uid))
            } else {
                repository.updateHabit(
                    habit.copy(
                        doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                )
                repository.addHabitDone(HabitDone(habitUid = habit.uid, date = date))
            }
        }
    }
}