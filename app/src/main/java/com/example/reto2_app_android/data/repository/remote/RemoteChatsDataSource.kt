package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.utils.Resource

class RemoteChatsDataSource: BaseDataSource(), CommonChatRepository {
    override  suspend fun getChats() = getResult {
        RetrofitClient.apiInterface.getChats()
    }


    override suspend fun createChat(chat: ChatResponse_Chat, userId: Int) = getResult {
        RetrofitClient.apiInterface.createChat(chat, userId)
    }
}