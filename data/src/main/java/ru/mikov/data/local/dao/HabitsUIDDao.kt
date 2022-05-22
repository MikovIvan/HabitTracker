package ru.mikov.habittracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.mikov.data.local.dao.BaseDao
import ru.mikov.habittracker.data.local.entities.HabitUIDEntity

@Dao
interface HabitsUIDDao : BaseDao<HabitUIDEntity> {

    @Query(
        """
            SELECT * FROM habit_uid
        """
    )
    suspend fun getAllUID(): List<HabitUIDEntity>

    @Query(
        """DELETE FROM habit_uid"""
    )
    suspend fun clear()
}