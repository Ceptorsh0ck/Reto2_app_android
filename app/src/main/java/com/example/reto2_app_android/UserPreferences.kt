package com.example.reto2_app_android

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.reto2_app_android.data.User
import com.google.gson.Gson

class UserPreferences() {

    private val sharedPreferences: SharedPreferences by lazy {
        MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    init {
        if (!isRememberMeEnabled()) {
            unLogUser()
        }
    }



    companion object {
        const val USER_TOKEN = "user_token"
        const val LOGGED_USER = "logged_user"
        const val REMEMBER_ME = "remember_me"
    }

    fun saveLoggedUser(user: User) {
        val editor = sharedPreferences.edit()

        val userJson = Gson().toJson(user)
        editor.putString(LOGGED_USER, userJson)
        editor.apply()
    }

    fun saveAuthToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_TOKEN,token)
        editor.apply()
    }
    fun isRememberMeEnabled(): Boolean {
        return sharedPreferences.getBoolean(REMEMBER_ME, false)
    }

    fun saveRememberMeStatus(rememberMe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(REMEMBER_ME, rememberMe)
        editor.apply()
    }




    fun unLogUser() {
        val editor = sharedPreferences.edit()
        editor.remove(USER_TOKEN)
        editor.remove(REMEMBER_ME)
        editor.remove(LOGGED_USER)
        editor.apply()
    }

    fun getLoggedUser(): User? {
        val userJson = sharedPreferences.getString(LOGGED_USER, null)
        return if (userJson != null) {
            // Aquí, utilizamos Gson para convertir el JSON almacenado de nuevo a un objeto User.
            Gson().fromJson(userJson, User::class.java)
        } else {
            null
        }
    }



    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(USER_TOKEN,null)
    }





}