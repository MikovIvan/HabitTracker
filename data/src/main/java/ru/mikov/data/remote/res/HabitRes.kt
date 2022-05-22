package ru.mikov.habittracker.data.remote.res

import com.squareup.moshi.Json

data class HabitRes(
    val color: Int,
    val count: Int,
    val date: Int,
    val description: String,
    @Json(name = "done_dates")
    val doneDates: List<Int>,
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val uid: String
)