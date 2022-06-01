package ru.mikov.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.mikov.data.local.dao.BaseDao
import ru.mikov.data.local.entities.HabitDoneEntity

@Dao
interface HabitDoneDao : BaseDao<HabitDoneEntity> {

    @Query(
        """
        SELECT * FROM habit_done
    """
    )
    suspend fun findAllHabitDone(): List<HabitDoneEntity>
}