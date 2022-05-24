package ru.mikov.data.remote.res

import com.squareup.moshi.Json

data class HabitRes(
    @Json(name = "color")
    val color: Int,
    @Json(name = "count")
    val count: Int,
    @Json(name = "date")
    val date: Int,
    @Json(name = "description")
    val description: String,
    @Json(name = "done_dates")
    val doneDates: List<Int>,
    @Json(name = "frequency")
    val frequency: Int,
    @Json(name = "priority")
    val priority: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "type")
    val type: Int,
    @Json(name = "uid")
    val uid: String
)