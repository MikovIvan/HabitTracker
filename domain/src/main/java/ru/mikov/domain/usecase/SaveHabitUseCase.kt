package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.mikov.domain.models.DELAY_BETWEEN_REQUEST
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitUID
import ru.mikov.domain.models.RETRY
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.repository.RootRepository
import java.io.IOException
import java.util.*
import javax.inject.Inject

class SaveHabitUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(habit: Habit, isConnected: Boolean): Flow<Resource<HabitUID>> {
        return flow<Resource<HabitUID>> {
            if (isConnected) {
                repository.uploadHabitToNetwork(habit)
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
                        repository.addHabitToDb(habit.copy(uid = it.uid))
                        emit(Resource.Success(data = it))
                    }
            } else {
                repository.addHabitToDb(
                    habit.copy(
                        uid = UUID.randomUUID().toString(),
                        isSynchronized = false
                    )
                )
            }
        }.flowOn(dispatcher)
    }
}