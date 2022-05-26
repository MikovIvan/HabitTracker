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

class SyncDeletedHabitsWithServerUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<Resource<HabitUID>> {
        return flow<Resource<HabitUID>> {
            val uids = repository.getUIDToDelete()
            uids.forEach { habitUID ->
                repository.deleteHabitFromNetwork(habitUID.uid)
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
                    .collect { emit(Resource.Success(data = habitUID)) }
            }
            repository.clearUID()
        }.flowOn(dispatcher)
    }
}