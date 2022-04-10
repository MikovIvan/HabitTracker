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
    fun findHabits(type: HabitType): LiveData<List<Habit>>

    @Query(
        """
        SELECT * FROM habits
        WHERE id = :id
    """
    )
    fun findHabitById(id: Int): Habit

}