package ru.mikov.habittracker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.mikov.habittracker.data.local.entities.Habit

@Dao
interface HabitsDao : BaseDao<Habit> {

    @Query(
        """
        SELECT * FROM habits
    """
    )
    fun findHabits(): LiveData<List<Habit>>

    @Query(
        """
        SELECT * FROM habits
        WHERE id = :id
    """
    )
    fun findHabitById(id: Int): Habit

    @RawQuery(observedEntities = [Habit::class])
    fun findArticlesByRaw(simpleSQLiteQuery: SimpleSQLiteQuery): LiveData<List<Habit>>
}