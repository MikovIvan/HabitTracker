package ru.mikov.data.remote

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

    companion object {
        const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"
        const val API_KEY = "21b5dfd9-f8ea-4018-b17e-a541a4d5aaf8"
    }
}