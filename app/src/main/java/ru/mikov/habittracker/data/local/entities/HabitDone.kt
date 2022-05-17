package ru.mikov.habittracker.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_done")
data class HabitDone(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    @ColumnInfo(name = "done_date")
    var doneDate: Int
)
