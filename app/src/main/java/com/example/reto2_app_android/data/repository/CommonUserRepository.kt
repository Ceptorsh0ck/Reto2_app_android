package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.utils.Resource

interface CommonUserRepository {
//    suspend fun getUsers(): Resource<List<User>>

    suspend fun loginUser(user: UserLogin): Resource<AuthenticationResponse>
}