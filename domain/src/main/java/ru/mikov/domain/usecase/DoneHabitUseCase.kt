package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.mikov.domain.models.DELAY_BETWEEN_REQUEST
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.RETRY
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.repository.RootRepository
import java.io.IOException
import java.util.*
import javax.inject.Inject

class DoneHabitUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(habit: Habit, isConnected: Boolean): Flow<Resource<Habit>> {
        return flow<Resource<Habit>> {
            val date = (Date().time / 1000).toInt()
            if (isConnected) {
                repository.doneHabit(HabitDone(date, habit.uid))
                    .retry(RETRY) {
                        delay(DELAY_BETWEEN_REQUEST)
                        return@retry true
                    }
                    .catch { e ->
                        when (e) {
                            is HttpException -> emit(
                                Resource.Error(
                                    e.localizedMessage ?: "An unexpected error occured"
                                )
                            )
                            is IOException -> emit(Resource.Error("Couldn't reach server. Check your internet connection."))
                        }
                    }
                    .collect {
                        repository.updateHabit(
                            habit.copy(
                                doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                        )
                        emit(Resource.Success(data = habit))
                    }

            } else {
                repository.updateHabit(
                    habit.copy(
                        doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                )
                repository.addHabitDone(HabitDone(habitUid = habit.uid, date = date))
                emit(Resource.Success(data = habit))
            }
        }.flowOn(dispatcher)
    }
}

