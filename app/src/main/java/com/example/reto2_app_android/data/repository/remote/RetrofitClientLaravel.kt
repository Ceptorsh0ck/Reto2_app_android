package com.example.reto2_app_android.data.repository.remote

import android.util.Log
import com.example.reto2_app_android.MyApp
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClientLaravel {

    // esta es la ip a la que vamos a hacer peticiones (es localhost pero desde el emulador no deja)
    // NOTE acordarse de que hace falta actualizar el manifest
    const val API_URI = "https://eloradmin.ddns.net/api/v1/"

    var client = OkHttpClient.Builder().addInterceptor {chain ->
        val authToken = MyApp.userPreferences.fetchAuthToken()
        if (authToken != null) {
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization","Bearer $authToken")
                .build()
            chain.proceed(newRequest)
        } else {
            val newRequest: Request = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }
    }.build()

    var gson = GsonBuilder()
        .setLenient() // Esto permite JSON mal formado
        .setDateFormat("yyyy-MM-dd")
        .create()

    // creamos el cliente de retrofit con la url de la api
    val retrofitClientLaravel: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(API_URI)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiInterfaceLaravel: APIInterfaceLaravel by lazy {
        retrofitClientLaravel
            .build()
            .create(APIInterfaceLaravel::class.java)
    }


}