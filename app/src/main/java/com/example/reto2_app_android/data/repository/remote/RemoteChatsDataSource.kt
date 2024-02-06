package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.ChatShow
import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.utils.Resource

class RemoteChatsDataSource: BaseDataSource(), CommonChatRepository {
    override suspend fun getChats() = getResult {
        RetrofitClient.apiInterface.getChats()
    }

    override suspend fun updateChat(idServer: Int, idRoom: Int): Resource<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(chat: ChatResponse_Chat): Resource<Int> {
        TODO("Not yet implemented")
    }


    override suspend fun createChatServer(chat: ChatResponse_Chat, userId: Int) = getResult {
        RetrofitClient.apiInterface.createChat(chat, userId)
    }

    override suspend fun addchat(chat: ChatResponse_Chat) {
            TODO("Not yet implemented")
    }


    override suspend fun deleteUserChat(message: AddPeopleResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getPublicChat(userId: Int) = getResult {
        RetrofitClient.apiInterface.getPublicChats()
    }
}
