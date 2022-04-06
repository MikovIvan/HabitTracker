package ru.mikov.habittracker.data.local.entities

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mikov.habittracker.R
import java.io.Serializable


@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var description: String,
    var priority: String,
    var type: HabitType,
    var periodicity: String,
    val color: Int,
    @ColumnInfo(name = "number_of_executions")
    var numberOfExecutions: String
) : Serializable

enum class HabitType(@StringRes val stringRes: Int, val numOfTab: Int) {
    BAD(R.string.bad, 1),
    GOOD(R.string.good, 0)
}








