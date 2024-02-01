package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.utils.Resource

interface CommonChatRepository {
    suspend fun getChats(): Resource<List<ChatResponse_Chat>>
    suspend fun createChat(chat: ChatResponse_Chat, userId: Int): Resource<Int>


}