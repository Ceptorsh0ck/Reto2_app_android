package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.repository.CommonChatRepository

class RemoteChatsDataSource: BaseDataSource(), CommonChatRepository {
    override  suspend fun getChats() = getResult {
        RetrofitClient.apiInterface.getChats()
    }
}