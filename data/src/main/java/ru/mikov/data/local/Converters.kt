package ru.mikov.habittracker.data.local

import androidx.room.TypeConverter
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType

class HabitTypeConverter {
    @TypeConverter
    fun toTypeHabit(value: String) = enumValueOf<HabitType>(value)

    @TypeConverter
    fun fromTypeHabit(value: HabitType) = value.name
}

class HabitPriorityConverter {
    @TypeConverter
    fun toPriorityHabit(value: String) = enumValueOf<HabitPriority>(value)

    @TypeConverter
    fun fromPriorityHabit(value: HabitPriority) = value.name
}

class DoneDatesConverter {
    @TypeConverter
    fun fromDoneDates(value: List<Int>): String {
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun toDoneDates(value: String): List<Int> {
        return when {
            value.isBlank() -> emptyList()
            else -> value.split(",").map { it.trim().toInt() }
        }
    }
}