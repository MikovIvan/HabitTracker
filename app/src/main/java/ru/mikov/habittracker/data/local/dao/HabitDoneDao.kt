package ru.mikov.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.mikov.habittracker.data.local.entities.HabitDone

@Dao
interface HabitDoneDao : BaseDao<HabitDone> {

    @Query(
        """
        SELECT * FROM habit_done
    """
    )
    suspend fun findAllHabitDone(): List<HabitDone>
}