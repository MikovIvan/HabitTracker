package ru.mikov.data.remote.res

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class HabitRes(
    @Json(name = "uid")
    val uid: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "priority")
    val priority: Int,
    @Json(name = "type")
    val type: Int,
    @Json(name = "count")
    val count: Int,
    @Json(name = "frequency")
    val frequency: Int,
    @Json(name = "color")
    val color: Int,
    @Json(name = "date")
    val date: Int,
    @Json(name = "done_dates")
    @SerializedName("done_dates")
    val doneDates: List<Int>,
)