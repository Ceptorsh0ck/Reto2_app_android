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

    }

    //se ejecuta cuando recibimos una notificacion de firebase
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.let {
            showNotification(it.title ?: "tit", it.body ?: "nada")

        }
        remoteMessage.data.isNotEmpty().let {

            val customData = convertDataCustomObject(remoteMessage.data)
        }


    }
    private fun convertDataCustomObject(data: Map<String, String>): NotificationData {
        return Gson().fromJson(Gson().toJson(data), NotificationData::class.java)
    }

    private fun showNotification(title: String, body: String){

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