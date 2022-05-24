package ru.mikov.data.mappers

import ru.mikov.data.remote.res.HabitRes
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitUID
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes

fun HabitRes.toDomain(): Habit {
    return Habit(
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

fun HabitDoneRes.toDomain(): HabitDone {
    return HabitDone(
        date = date,
        habitUid = habitUid
    )
}

fun HabitUIDRes.toDomain(): HabitUID {
    return HabitUID(
        uid = uid
    )
}