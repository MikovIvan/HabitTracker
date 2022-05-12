package ru.mikov.habittracker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType

@Dao
interface HabitsDao : BaseDao<Habit> {

    @Query(
        """
        SELECT * FROM habits
        WHERE type = :type
    """
    )
    fun findHabitsByType(type: HabitType): LiveData<List<Habit>>

    @Query(
        """
        SELECT * FROM habits
        WHERE id = :id
    """
    )
    fun findHabitById(id: String): LiveData<Habit?>

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
    suspend fun findUnSyncHabits(): List<Habit>

    @Query(
        """
        SELECT * FROM habits
    """
    )
    suspend fun findAllHabits(): List<Habit>
}