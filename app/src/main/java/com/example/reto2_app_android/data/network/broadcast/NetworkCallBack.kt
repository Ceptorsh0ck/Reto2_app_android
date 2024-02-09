package com.example.reto2_app_android.data.network.broadcast

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.widget.Toast

class NetworkCallBack (private val context: Context) : ConnectivityManager.NetworkCallback() {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun register() {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    private fun showToast(message: String) {
    }

    override fun onAvailable(network: Network) {
    }

    override fun onLost(network: Network) {
    }
}