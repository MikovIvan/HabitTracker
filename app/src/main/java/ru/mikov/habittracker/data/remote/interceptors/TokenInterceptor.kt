package ru.mikov.habittracker.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.mikov.habittracker.BuildConfig

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request =
            chain.request().newBuilder().addHeader("Authorization", BuildConfig.API_KEY).build()
        return chain.proceed(request)
    }
}