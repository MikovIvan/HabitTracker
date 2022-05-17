package ru.mikov.habittracker.data.local.entities

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mikov.habittracker.App
import ru.mikov.habittracker.R
import java.io.Serializable


@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String,
    var name: String,
    var description: String,
    var priority: HabitPriority,
    var type: HabitType,
    //number of days
    var periodicity: String,
    var date: Long,
    val color: Int,
    //number of executions per day
    @ColumnInfo(name = "number_of_executions")
    var numberOfExecutions: String,
    @ColumnInfo(name = "is_synchronized")
    var isSynchronized: Boolean = true,
    @ColumnInfo(name = "done_dates")
    var doneDates: List<Int> = emptyList()
) : Serializable {

    var totalCountOfExecutions: Int = numberOfExecutions.toInt() * periodicity.toInt()

    fun getLeftToDo(): Int {
        return totalCountOfExecutions - doneDates.size
    }
}

enum class HabitType(@StringRes val stringRes: Int, val numOfTab: Int, val id: Int) {
    BAD(R.string.bad, 1, 1),
    GOOD(R.string.good, 0, 0);

    companion object {
        fun getById(id: Int) = values()[id]
    }
}

enum class HabitPriority(@StringRes val stringRes: Int, val id: Int) {
    HIGH(R.string.high, 0),
    MEDIUM(R.string.medium, 1),
    LOW(R.string.low, 2);

    override fun toString(): String {
        return App.applicationContext().getString(stringRes)
    }

    companion object {
        fun fromString(value: String) = values().first { it.toString() == value }

        fun getById(id: Int) = values()[id]
    }
}








