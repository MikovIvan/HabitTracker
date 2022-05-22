package ru.mikov.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.mikov.data.remote.RestService.Companion.API_KEY

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request =
            chain.request().newBuilder().addHeader("Authorization", API_KEY).build()
        return chain.proceed(request)
    }
}