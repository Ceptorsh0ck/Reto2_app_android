package com.example.reto2_app_android.data.network.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.example.reto2_app_android.ui.MainActivity

class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val networkInfo = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (networkInfo != null && networkInfo.isConnected) {
                // Se detectó una conexión de red
                // Actualizar el ícono del menú aquí
                //(context as? MainActivity)?.updateWifiIcon(true)
            } else {
                // No hay conexión de red
                // Actualizar el ícono del menú aquí
                //(context as? MainActivity)?.updateWifiIcon(false)
            }
        }
    }
}