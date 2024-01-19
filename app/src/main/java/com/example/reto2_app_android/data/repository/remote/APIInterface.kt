package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIInterface {

    @GET("sockets/chats")
    suspend fun getChats(): Response<List<Chat>>

    @POST("auth/login")
    suspend fun loginUser(@Body user: UserLogin): Response<AuthenticationResponse>


}