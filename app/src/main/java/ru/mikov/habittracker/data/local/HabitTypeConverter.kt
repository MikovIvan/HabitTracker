package ru.mikov.habittracker.data.local

import androidx.room.TypeConverter
import ru.mikov.habittracker.data.local.entities.HabitPriority
import ru.mikov.habittracker.data.local.entities.HabitType

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