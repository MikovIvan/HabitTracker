package ru.mikov.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mikov.data.local.dao.BaseDao
import ru.mikov.data.local.entities.HabitEntity
import ru.mikov.data.local.entities.HabitType

@Dao
interface HabitsDao : BaseDao<HabitEntity> {

    @Query(
        """
        SELECT * FROM habits
        WHERE type = :type
    """
    )
    fun findHabitsByType(type: HabitType): Flow<List<HabitEntity>>

    @Query(
        """
        SELECT * FROM habits
        WHERE id = :id
    """
    )
    fun findHabitById(id: String): Flow<HabitEntity?>

    @Query(
        """
            DELETE FROM habits
            WHERE id = :id
        """
    )
    suspend fun deleteHabit(id: String)

    @Query(
        """
            SELECT * FROM habits
            WHERE is_synchronized = 0
        """
    )
    suspend fun findUnSyncHabits(): List<HabitEntity>

    @Query(
        """
        SELECT * FROM habits
    """
    )
    suspend fun findAllHabits(): List<HabitEntity>
}