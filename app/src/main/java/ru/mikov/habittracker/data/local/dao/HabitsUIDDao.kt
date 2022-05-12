package ru.mikov.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.mikov.habittracker.data.local.entities.HabitUID

@Dao
interface HabitsUIDDao : BaseDao<HabitUID> {

    @Query(
        """
            SELECT * FROM habit_uid
        """
    )
    suspend fun getAllUID(): List<HabitUID>

    @Query(
        """DELETE FROM habit_uid"""
    )
    suspend fun clear()
}