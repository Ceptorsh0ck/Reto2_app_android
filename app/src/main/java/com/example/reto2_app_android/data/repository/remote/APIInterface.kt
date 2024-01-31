package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.model.ChatResponse_UserMessage
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

    @GET("{chatId}/getUser")
    suspend fun getAllUsersToInsertIntoChat(@Path("chatId") chatId: Int): Response<List<AddPeople>>

    @POST("chats/{chatId}/add-users")
    suspend fun addUsersToChats(@Path("chatId") chatId: Int, @Body list: List<AddPeopleResponse>)


}