package ru.mikov.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_done")
data class HabitDoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    @ColumnInfo(name = "done_date")
    var doneDate: Int
)
