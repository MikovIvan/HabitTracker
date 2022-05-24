package ru.mikov.data.mappers

import ru.mikov.data.remote.res.HabitRes
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitUID
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes

fun Habit.toResponse(): HabitRes {
    return HabitRes(
        uid = uid,
        title = title,
        description = description,
        priority = priority,
        type = type,
        frequency = frequency,
        date = date,
        color = color,
        count = count,
        doneDates = doneDates
    )
}

fun HabitDone.toResponse(): HabitDoneRes {
    return HabitDoneRes(
        date = date,
        habitUid = habitUid
    )
}

fun HabitUID.toResponse(): HabitUIDRes {
    return HabitUIDRes(
        uid = uid
    )
}