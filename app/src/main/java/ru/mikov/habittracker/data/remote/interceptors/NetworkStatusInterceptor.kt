package ru.mikov.habittracker.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.mikov.habittracker.data.remote.NetworkMonitor
import ru.mikov.habittracker.data.remote.err.NoNetworkError

class NetworkStatusInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkMonitor.isConnected) throw NoNetworkError()
        return chain.proceed(chain.request())
    }
}