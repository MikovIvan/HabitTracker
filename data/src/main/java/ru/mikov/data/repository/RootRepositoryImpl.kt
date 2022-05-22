package ru.mikov.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mikov.data.mappers.toDomain
import ru.mikov.data.mappers.toEntity
import ru.mikov.data.mappers.toResponse
import ru.mikov.data.remote.RestService
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.models.HabitUID
import ru.mikov.domain.repository.RootRepository
import ru.mikov.habittracker.data.local.dao.HabitDoneDao
import ru.mikov.habittracker.data.local.dao.HabitsDao
import ru.mikov.habittracker.data.local.dao.HabitsUIDDao
import ru.mikov.habittracker.data.remote.res.HabitUIDRes

class RootRepositoryImpl(
    private var habitsDao: HabitsDao,
    private var habitsUIDDao: HabitsUIDDao,
    private var habitDoneDao: HabitDoneDao,
    private val api: RestService
) : RootRepository {
    override suspend fun addHabitToDb(habit: Habit) {
        habitsDao.insert(habit.toEntity())
    }

    override suspend fun addHabitsToDb(habit: List<Habit>) {
        habitsDao.insert(habit.map { it.toEntity() })
    }

    override suspend fun deleteHabitFromDb(habitId: String) {
        habitsDao.deleteHabit(habitId)
    }

    override suspend fun addHabitUID(habitUID: HabitUID) {
        habitsUIDDao.insert(habitUID.toEntity())
    }

    override fun getHabit(id: String): Flow<Habit?> {
        return habitsDao.findHabitById(id).map { it?.toDomain() }
    }

    override fun getHabitsByType(typeDomain: HabitTypeDomain): Flow<List<Habit>> {
        return habitsDao.findHabitsByType(typeDomain.toEntity()).map { it.map { it.toDomain() } }
    }

    override suspend fun updateHabit(updatedHabit: Habit) {
        habitsDao.update(updatedHabit.toEntity())
    }

    override suspend fun uploadHabitToNetwork(habit: Habit): HabitUID {
        return api.addHabit(habit.toResponse()).toDomain()
    }

    override suspend fun loadHabitsFromNetwork(): List<Habit> {
        return api.habits().map { it.toDomain() }
    }

    override suspend fun deleteHabitFromNetwork(habitUID: String) {
        api.deleteHabit(HabitUIDRes(habitUID))
    }

    override suspend fun isNoHabits(): Boolean {
        return habitsDao.findAllHabits().isEmpty()
    }

    override suspend fun getUnSyncHabits(): List<Habit> {
        return habitsDao.findUnSyncHabits().map { it.toDomain() }
    }

    override suspend fun getUIDToDelete(): List<HabitUID> {
        return habitsUIDDao.getAllUID().map { it.toDomain() }
    }

    override suspend fun clearUID() {
        return habitsUIDDao.clear()
    }

    override suspend fun doneHabit(habitDoneRes: HabitDone) {
        api.completeHabit(habitDoneRes.toResponse())
    }

    override suspend fun getUnSyncDoneDates(): List<HabitDone> {
        return habitDoneDao.findAllHabitDone().map { it.toDomain() }
    }

    override suspend fun addHabitDone(habitDone: HabitDone) {
        habitDoneDao.insert(habitDone.toEntity())
    }

    override suspend fun deleteDoneDate(habitDone: HabitDone) {
        habitDoneDao.delete(habitDone.toEntity())
    }
}