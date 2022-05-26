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
import javax.inject.Inject

class UploadUnSyncHabitsToServerUseCase @Inject constructor(
    private val repository: RootRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<Resource<Habit>> {
        return flow<Resource<Habit>> {
            val unSyncHabits = repository.getUnSyncHabits()
            unSyncHabits.forEach { habit ->
                syncHabit(habit)
                emit(Resource.Success(data = habit))
            }
            val unSyncDoneDates = repository.getUnSyncDoneDates()
            unSyncDoneDates.forEach { habitDone ->
                syncDoneDates(habitDone)
            }
        }.flowOn(dispatcher)
    }

    private suspend fun syncHabit(habit: Habit) {
        repository.uploadHabitToNetwork(habit.copy(uid = ""))
            .retry(RETRY) {
                delay(DELAY_BETWEEN_REQUEST)
                return@retry true
            }
            .catch { e ->
                when (e) {
                    is HttpException -> e.localizedMessage ?: "An unexpected error occured"
                    is IOException -> "Couldn't reach server. Check your internet connection."
                }
            }
            .collect {
                repository.deleteHabitFromDb(habit.uid)
                repository.addHabitToDb(habit.copy(uid = it.uid, isSynchronized = true))
            }
    }

    private suspend fun syncDoneDates(habitDone: HabitDone) {
        repository.doneHabit(habitDone)
            .retry(RETRY) {
                delay(DELAY_BETWEEN_REQUEST)
                return@retry true
            }
            .catch { e ->
                when (e) {
                    is HttpException -> e.localizedMessage ?: "An unexpected error occured"
                    is IOException -> "Couldn't reach server. Check your internet connection."
                }
            }
            .collect {
                repository.deleteDoneDate(habitDone)
            }
    }
}