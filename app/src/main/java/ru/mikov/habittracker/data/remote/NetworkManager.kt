package ru.mikov.habittracker.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.mikov.habittracker.data.remote.interceptors.ErrorStatusInterceptor
import ru.mikov.habittracker.data.remote.interceptors.NetworkStatusInterceptor
import ru.mikov.habittracker.data.remote.interceptors.TokenInterceptor

object NetworkManager {

    private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: RestService by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient().newBuilder()
            .addInterceptor(TokenInterceptor())
            .addInterceptor(logging)
            .addInterceptor(NetworkStatusInterceptor())
            .addInterceptor(ErrorStatusInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()

        retrofit.create(RestService::class.java)
    }
}