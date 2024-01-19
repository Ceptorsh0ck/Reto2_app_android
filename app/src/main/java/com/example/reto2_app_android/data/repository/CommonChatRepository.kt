package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.model.ChatResponse_UserMessage
import com.example.reto2_app_android.utils.Resource

interface CommonChatRepository {
    suspend fun getChats(): Resource<ChatResponse_User>
}