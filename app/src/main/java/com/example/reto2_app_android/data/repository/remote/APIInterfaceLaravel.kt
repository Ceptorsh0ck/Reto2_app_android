package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.ui.auth.PasswordRecoverRequest
import com.squareup.moshi.Json
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterfaceLaravel {

    @POST("password/reset")
    suspend fun recoverPassword(@Body request: PasswordRecoverRequest): Response<ResponseBody>







}