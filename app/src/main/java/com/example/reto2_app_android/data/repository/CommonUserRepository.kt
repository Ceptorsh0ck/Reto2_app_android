package com.example.reto2_app_android.data.repository

import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.ui.auth.PasswordRecoverRequest
import com.example.reto2_app_android.utils.Resource
import okhttp3.ResponseBody

interface CommonUserRepository {

    suspend fun loginUser(user: UserLogin): Resource<AuthenticationResponse>
    suspend fun registerUser(user: UserNew): Resource<UserNew>
    suspend fun getUserIdWithIdServer(userid: Int): Resource<Int>
    suspend fun recoverPassword(email: PasswordRecoverRequest): Resource<ResponseBody>
}