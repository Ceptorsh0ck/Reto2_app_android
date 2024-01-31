package com.example.reto2_app_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class MyFireBaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFireBaseMgsService"
    private val CHANNEL_ID = "MyNotificationChannel"
    override fun onNewToken(token: String) {
        //metodo se llama automaticamente cuando se genera un nuevo token
        Log.d(TAG, "Nuevo token :$token")

    }

    //se ejecuta cuando recibimos una notificacion de firebase
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "mensaje recibido de :${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d(TAG, "notificacion: ${it.title}")
            Log.d(TAG, "notificacion: ${it.body}")
            showNotification(it.title ?: "tit", it.body ?: "nada")

        }
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "data: ${remoteMessage.data}")

            val customData = convertDataCustomObject(remoteMessage.data)
            Log.d(TAG, customData.anotherKey)
        }


    }
    private fun convertDataCustomObject(data: Map<String, String>): NotificationData {
        Log.d(TAG, "cuustom")
        return Gson().fromJson(Gson().toJson(data), NotificationData::class.java)
    }

    private fun showNotification(title: String, body: String){
        Log.d(TAG, "showNotification")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID, "canal de notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        //Crear objeto de notificacion
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        //mostrar noti
        notificationManager.notify(1, builder.build())

    }


}