package ru.mikov.habittracker.data.entities

import androidx.annotation.StringRes
import ru.mikov.habittracker.R
import java.io.Serializable


data class Habit(
    val id: String,
    var name: String,
    var description: String,
    var priority: String,
    var type: HabitType,
    var periodicity: String,
    val color: Int,
    var numberOfExecutions: String
) : Serializable

enum class HabitType(@StringRes val stringRes: Int) {
    BAD(R.string.bad),
    GOOD(R.string.good)
}








