package ru.mikov.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    var name: String,
    var description: String,
    var priority: HabitPriority,
    var type: HabitType,
    //number of days
    var periodicity: Int,
    var date: Int,
    val color: Int,
    //number of executions per day
    @ColumnInfo(name = "number_of_executions")
    var numberOfExecutions: Int,
    @ColumnInfo(name = "is_synchronized")
    var isSynchronized: Boolean = true,
    @ColumnInfo(name = "done_dates")
    var doneDates: List<Int> = emptyList()
) : Serializable {

    var totalCountOfExecutions: Int = numberOfExecutions * periodicity

    fun getLeftToDo(): Int {
        return totalCountOfExecutions - doneDates.size
    }
}

enum class HabitType(val numOfTab: Int, val id: Int) {
    GOOD(0, 0),
    BAD(1, 1);

    companion object {
        fun getById(id: Int) = values()[id]
    }
}

enum class HabitPriority(val id: Int) {
    HIGH(0),
    MEDIUM(1),
    LOW(2);

    companion object {
        fun getById(id: Int) = values()[id]
    }
}








