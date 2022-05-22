package ru.mikov.data.mappers

import ru.mikov.data.local.entities.HabitEntity
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType
import ru.mikov.domain.models.Habit
import ru.mikov.domain.models.HabitDone
import ru.mikov.domain.models.HabitTypeDomain
import ru.mikov.domain.models.HabitUID
import ru.mikov.habittracker.data.local.entities.HabitDoneEntity
import ru.mikov.habittracker.data.local.entities.HabitUIDEntity

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = uid,
        name = title,
        description = description,
        priority = HabitPriority.getById(priority),
        type = HabitType.getById(type),
        periodicity = frequency,
        date = date,
        color = color,
        numberOfExecutions = count,
        doneDates = doneDates
    )
}

fun HabitDone.toEntity(): HabitDoneEntity {
    return HabitDoneEntity(
        uid = habitUid,
        doneDate = date
    )
}

fun HabitUID.toEntity(): HabitUIDEntity {
    return HabitUIDEntity(
        id = uid
    )
}

fun HabitTypeDomain.toEntity(): HabitType {
    return when (this) {
        HabitTypeDomain.BAD -> HabitType.BAD
        HabitTypeDomain.GOOD -> HabitType.GOOD
    }
}