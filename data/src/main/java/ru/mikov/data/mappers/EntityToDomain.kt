package ru.mikov.data.mappers

import ru.mikov.data.local.entities.HabitEntity
import ru.mikov.data.local.entities.HabitType
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.models.HabitUID
import ru.mikov.habittracker.data.local.entities.HabitDoneEntity
import ru.mikov.habittracker.data.local.entities.HabitUIDEntity

fun HabitEntity.toDomain(): Habit {
    return Habit(
        uid = id,
        title = name,
        description = description,
        priority = priority.id,
        type = type.id,
        frequency = periodicity,
        date = date,
        color = color,
        count = numberOfExecutions,
        doneDates = doneDates
    )
}

fun HabitDoneEntity.toDomain(): HabitDone {
    return HabitDone(
        habitUid = uid,
        date = doneDate
    )
}

fun HabitUIDEntity.toDomain(): HabitUID {
    return HabitUID(
        uid = id
    )
}

fun HabitType.toDomain(): HabitTypeDomain {
    return when (this) {
        HabitType.BAD -> HabitTypeDomain.BAD
        HabitType.GOOD -> HabitTypeDomain.GOOD
    }
}