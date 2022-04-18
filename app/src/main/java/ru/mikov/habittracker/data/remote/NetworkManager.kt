package ru.mikov.habittracker.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.mikov.habittracker.data.remote.JsonConverter.moshi
import ru.mikov.habittracker.data.remote.interceptors.TokenInterceptor

object NetworkManager {

    private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"

    val api: RestService by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient().newBuilder()
            .addInterceptor(TokenInterceptor())
            .addInterceptor(logging)
            .build()

        //retrofit
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()

        retrofit.create(RestService::class.java)
    }
}