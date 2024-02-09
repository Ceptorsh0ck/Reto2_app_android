package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.utils.allowAll.hostnameVerifier
import com.example.reto2_app_android.utils.allowAll.trustManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager


object RetrofitClient {

    // esta es la ip a la que vamos a hacer peticiones (es localhost pero desde el emulador no deja)
    // NOTE acordarse de que hace falta actualizar el manifest

    const val API_URI = "https://10.5.7.15:443/api/"

    val sslContext = createSocketFactory()


    var client = OkHttpClient.Builder().hostnameVerifier(hostnameVerifier).sslSocketFactory(sslContext.socketFactory, trustManager).addInterceptor {chain ->
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
        .setDateFormat("yyyy-MM-dd")
        .create()

    // creamos el cliente de retrofit con la url de la api
    val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(API_URI)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiInterface: APIInterface by lazy {
        retrofitClient
            .build()
            .create(APIInterface::class.java)
    }

    private fun createSocketFactory(): SSLContext {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        return sslContext
    }

}