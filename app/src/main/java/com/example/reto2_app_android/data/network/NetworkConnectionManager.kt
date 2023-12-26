package com.example.reto2_app_android.data.network

import kotlinx.coroutines.flow.StateFlow

// Utiliza javax y blade, los cuales utilizan una version distinta de java que Room

interface NetworkConnectionManager {
    /**
     * Emits [Boolean] value when the current network becomes available or unavailable.
     */
    val isNetworkConnectedFlow: StateFlow<Boolean>

    val isNetworkConnected: Boolean

    fun startListenNetworkState()

    fun stopListenNetworkState()
}

