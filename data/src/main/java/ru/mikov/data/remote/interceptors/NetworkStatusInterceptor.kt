package ru.mikov.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.mikov.data.remote.NetworkMonitor
import ru.mikov.data.remote.NetworkMonitor.Companion.isConnected
import ru.mikov.habittracker.data.remote.err.NoNetworkError

class NetworkStatusInterceptor(val monitor: NetworkMonitor) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) throw NoNetworkError()
        return chain.proceed(chain.request())
    }
}