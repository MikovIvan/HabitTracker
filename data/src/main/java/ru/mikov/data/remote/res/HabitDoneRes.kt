package ru.mikov.habittracker.data.remote.res

import com.squareup.moshi.Json

data class
HabitDoneRes(
    val date: Int,
    @Json(name = "habit_uid")
    val habitUid: String
)