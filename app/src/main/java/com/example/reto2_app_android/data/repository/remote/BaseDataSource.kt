package com.example.reto2_app_android.data.repository.remote

import android.util.Log
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.utils.Resource
import retrofit2.Response

abstract class BaseDataSource {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Resource.success(body)

                }else{
                    return Resource.success()
                }
            }
            // podria no ser un error ...
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T> {
        return Resource.error("Network call has failed for a following reason: $message")
    }
}