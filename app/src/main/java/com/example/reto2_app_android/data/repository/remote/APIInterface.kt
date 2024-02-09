package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.ChatShow
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.model.ChatResponse_UserMessage
import com.example.reto2_app_android.ui.auth.PasswordRecoverRequest
import com.example.reto2_app_android.utils.Resource
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIInterface {

    @GET("chats")
    suspend fun getChats(): Response<List<ChatResponse_Chat>>
    @POST("auth/login")
    suspend fun loginUser(@Body user: UserLogin): Response<AuthenticationResponse>

    @GET("{chatId}/getUserToAdd")
    suspend fun getAllUsersToInsertIntoChat(@Path("chatId") chatId: Int): Response<List<AddPeople>>

    @GET("{chatId}/getUserToDelete")
    suspend fun getAllUsersToDeleteIntoChat(@Path("chatId") chatId: Int): Response<List<AddPeople>>

    @PUT("auth/register")
    suspend fun registerUser(@Body user: UserNew): Response<UserNew>

    @POST("chats/{userId}")
    suspend fun createChat(@Body chat: ChatResponse_Chat, @Path("userId") userId: Int): Response<ChatResponse_Chat>

    @DELETE("chats/{id}")
    suspend fun deleteChat(@Path("id") id: Int): Response<Void>

    @GET("chats/public")
    suspend fun getPublicChats(): Response<List<ChatShow>>
}