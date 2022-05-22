package ru.mikov.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

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
                    if (hasInternetConnected(context)) {
                        isConnected = true
                        isConnectedLive.postValue(true)
                    } else {
                        isConnected = false
                        isConnectedLive.postValue(false)
                    }
                }
            }
        )
    }

    fun hasInternetConnected(context: Context): Boolean {
        try {
            val connection = URL("https://www.google.com").openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Test")
            connection.setRequestProperty("Connection", "close")
            connection.connectTimeout = 1500 // configurable
            connection.connect()
            Log.d("InternetConnected", "hasInternetConnected: ${(connection.responseCode == 200)}")
            return (connection.responseCode == 200)
        } catch (e: IOException) {
            Log.e("InternetConnected", "Error checking internet connection", e)
        }
        Log.d("InternetConnected", "hasInternetConnected: false")
        return false
    }

}
