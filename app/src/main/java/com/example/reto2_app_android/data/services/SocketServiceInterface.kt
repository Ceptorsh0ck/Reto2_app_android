package com.example.reto2_app_android.data.services

interface SocketServiceInterface {
    fun onSaveMessage(message: String, group: String, idServer: Int)

}