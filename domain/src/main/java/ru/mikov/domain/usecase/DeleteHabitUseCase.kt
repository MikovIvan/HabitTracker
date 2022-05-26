package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.mikov.domain.models.DELAY_BETWEEN_REQUEST
import ru.mikov.domain.models.HabitUID
import ru.mikov.domain.models.RETRY
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.repository.RootRepository
import java.io.IOException
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        habitId: String,
        isConnected: Boolean,
        isSynchronized: Boolean
    ): Flow<Resource<String>> {
        return flow<Resource<String>> {
            when {
                //если есть интернет
                isConnected -> {
                    repository.deleteHabitFromNetwork(habitId)
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
                            repository.deleteHabitFromDb(habitId)
                            emit(Resource.Success(data = habitId))
                        }
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
        }.flowOn(dispatcher)
    }
}