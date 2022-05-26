package ru.mikov.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.mikov.domain.models.DELAY_BETWEEN_REQUEST
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.RETRY
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.repository.RootRepository
import java.io.IOException
import javax.inject.Inject

class LoadHabitsUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<Resource<List<Habit>>> = flow<Resource<List<Habit>>> {
        emit(Resource.Loading(true))
        repository.loadHabitsFromNetwork()
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
                repository.addHabitsToDb(it)
                emit(Resource.Success(data = it))
            }
    }.flowOn(dispatcher)
}