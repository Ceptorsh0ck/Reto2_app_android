package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.ChatShow
import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.utils.Resource
import retrofit2.Response

interface CommonChatRepository {
    suspend fun getChats(): Resource<List<ChatResponse_Chat>>

    suspend fun updateChat(idServer: Int, idRoom: Int): Resource<Int>

    suspend fun createChat(chat: ChatResponse_Chat): Resource<Int>
    suspend fun createChatServer(chat: ChatResponse_Chat, userId: Int): Resource<ChatResponse_Chat>
    suspend fun addchat(chat: ChatResponse_Chat)
    suspend fun deleteUserChat(message: AddPeopleResponse)
    suspend fun getPublicChat(userId: Int): Resource<List<ChatShow>>
}