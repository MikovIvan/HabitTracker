package ru.mikov.habittracker.data.remote

import retrofit2.http.*
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.remote.res.HabitRes
import ru.mikov.habittracker.data.remote.res.HabitUIDRes

interface RestService {

    @GET("habit")
    suspend fun habits(): List<HabitRes>

    @PUT("habit")
    suspend fun addHabit(
        @Body habitRes: HabitRes
    ): HabitUIDRes

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(
        @Body habitUIDRes: HabitUIDRes
    )

    @POST("habit_done")
    suspend fun completeHabit(
        @Body habitDoneRes: HabitDoneRes
    )

}