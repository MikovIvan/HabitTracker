package ru.mikov.habittracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_uid")
data class HabitUID(
    @PrimaryKey
    val id: String
)