package ru.mikov.habittracker.data

import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitPriority
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.remote.res.HabitRes

fun HabitRes.toHabit(): Habit =
    Habit(
        id = uid,
        name = title,
        description = description,
        priority = HabitPriority.getById(priority),
        type = HabitType.getById(type),
        periodicity = frequency.toString(),
        color = color,
        numberOfExecutions = count.toString(),
        date = date.toLong()
    )

fun Habit.toHabitRes(): HabitRes =
    HabitRes(
        color = color,
        count = numberOfExecutions.toInt(),
        date = date.toInt(),
        description = description,
        doneDates = emptyList(),
        frequency = periodicity.toInt(),
        priority = priority.id,
        title = name,
        type = type.id,
        uid = id
    )