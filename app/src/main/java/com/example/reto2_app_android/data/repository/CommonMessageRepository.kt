package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketMessageRes

interface CommonMessageRepository {
    suspend fun insertMessage(message: RoomMessages): Resource<Int>

    suspend fun getAllMessagesById(idUser: Int): Resource<List<MessageAdapter>>

    suspend fun updateMessage(message: SocketMessageResUpdate): Resource<List<MessageAdapter>>

}