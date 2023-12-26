package com.example.reto2_app_android.network

import android.content.ContentValues.TAG
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class NetworkCallback {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network : Network) {
            Log.e(TAG, "The default network is now: " + network)
        }

        override fun onLost(network : Network) {
            Log.e(TAG, "The application no longer has a default network. The last default network was " + network)
        }

        override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
            Log.e(TAG, "The default network changed capabilities: " + networkCapabilities)
        }

        override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
            Log.e(TAG, "The default network changed link properties: " + linkProperties)
        }
    })

}