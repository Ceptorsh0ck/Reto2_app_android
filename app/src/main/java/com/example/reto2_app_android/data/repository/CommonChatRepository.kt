package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.model.ChatResponse_UserMessage
import com.example.reto2_app_android.utils.Resource

interface CommonChatRepository {
    suspend fun getChats(): Resource<List<ChatResponse_Chat>>

    suspend fun addchat(chat: ChatResponse_Chat)
    suspend fun deleteUserChat(message: AddPeopleResponse)
}