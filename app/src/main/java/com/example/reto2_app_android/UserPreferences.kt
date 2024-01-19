package com.example.reto2_app_android

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class UserPreferences() {

    private val sharedPreferences: SharedPreferences by lazy {
        MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }




    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_LOGIN = "user_login"
        const val USER_PASSWORD = "user_password"
    }

    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_LOGIN,username)
        editor.apply()
    }

    fun saveAuthToken(token: String, login: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_LOGIN,login.substring(0,1).uppercase() + login.substring(1).lowercase())
        editor.putString(USER_TOKEN,token)
        editor.apply()
    }

    fun saveRememberMe(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_PASSWORD,password)
        editor.apply()
    }

    fun removeRememberMe() {
        val editor = sharedPreferences.edit()
        editor.remove(USER_PASSWORD)
        editor.apply()
    }

    fun logOut(){
        val editor = sharedPreferences.edit()
        editor.remove(USER_TOKEN)
        editor.remove(USER_ID)
        editor.remove(USER_LOGIN)
        editor.remove(USER_PASSWORD)
        editor.apply()
    }

    fun fetchLogin(): String? {
        return sharedPreferences.getString(USER_LOGIN,null)
    }

    fun fetchPassword(): String? {
        return sharedPreferences.getString(USER_PASSWORD,null)
    }

    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(USER_TOKEN,null)
    }


    fun fetchUserId(): Int? {
        return sharedPreferences.getInt(USER_ID,0)
    }


}