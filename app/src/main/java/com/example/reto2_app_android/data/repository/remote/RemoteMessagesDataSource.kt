package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketMessageReq

class RemoteMessagesDataSource: BaseDataSource(), CommonMessageRepository {
    override suspend fun insertMessage(message: RoomMessages): Resource<List<MessageAdapter>> {
        TODO("Not yet implemented")
    }
    override suspend fun getAllMessagesById(idUser: Int): Resource<List<MessageAdapter>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMessage(message: SocketMessageResUpdate): Resource<List<MessageAdapter>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessagesNoSended(): Resource<List<SocketMessageReq>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsersToInsertIntoChat(idChat: Int) = getResult {
        RetrofitClient.apiInterface.getAllUsersToInsertIntoChat(idChat)
    }

    override suspend fun getAllUsersToDeleteIntoChat(idChat: Int)= getResult {
        RetrofitClient.apiInterface.getAllUsersToDeleteIntoChat(idChat)
    }


    override suspend fun isAdmin(chatId: Int, userId: Int?): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChat(id: Int) {
        RetrofitClient.apiInterface.deleteChat(id)
    }
}