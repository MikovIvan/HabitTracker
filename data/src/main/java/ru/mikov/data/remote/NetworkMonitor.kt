package ru.mikov.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class NetworkMonitor(val context: Context) {

    companion object {
        var isConnected: Boolean = false
        val isConnectedLive = MutableLiveData(false)
    }

    private lateinit var cm: ConnectivityManager

    fun registerNetworkMonitor() {
        cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    isConnected = false
                    isConnectedLive.postValue(false)
                }

                override fun onAvailable(network: Network) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (InternetAvailability.check()) {
                            withContext(Dispatchers.Main) {
                                isConnected = true
                                isConnectedLive.postValue(true)
                            }
                        } else {
                            isConnected = false
                            isConnectedLive.postValue(false)
                        }
                    }
                }
            }
        )
    }
}

object InternetAvailability {

    fun check(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53))
            socket.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
