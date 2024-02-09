package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes

interface CommonMessageRepository {
    suspend fun insertMessage(message: RoomMessages): Resource<List<MessageAdapter>>



    suspend fun getAllMessagesById(idUser: Int): Resource<List<MessageAdapter>>

    suspend fun updateMessage(message: SocketMessageResUpdate): Resource<List<MessageAdapter>>
    suspend fun getMessagesNoSended(): Resource<List<SocketMessageReq>>

    suspend fun getAllUsersToInsertIntoChat(idChat: Int): Resource<List<AddPeople>>

    suspend fun getAllUsersToDeleteIntoChat(idChat: Int): Resource<List<AddPeople>>

    suspend fun isAdmin(chatId: Int, userId: Int?): Resource<Boolean>
    suspend fun deleteChat(id: Int)

}