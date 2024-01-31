package com.example.reto2_app_android.utils

import android.util.Base64
import android.util.Log
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.model.Role
import org.json.JSONObject
import java.io.UnsupportedEncodingException

object JWTUtils {
    @Throws(Exception::class)
    fun decoded(token: String): User? {
        try {
            val tokenBody = token.split(".")[1]
            val decodedTokenString = getJson(tokenBody)
            val jsonObject = JSONObject(decodedTokenString)
            Log.d("JON", "decoded: $jsonObject")
            val id = jsonObject.getInt("id")
            val name = jsonObject.getString("name")
            val surname = jsonObject.getString("surname1")
            val email = jsonObject.getString("email")
            val firstLogin = jsonObject.getBoolean("firstLogin")

            val listRolesJsonArray = jsonObject.getJSONArray("listRoles")
            val roles = mutableListOf<Role>()

            for (i in 0 until listRolesJsonArray.length()) {
                val roleJsonObject = listRolesJsonArray.getJSONObject(i)
                val roleId = roleJsonObject.getInt("id")
                val roleName = roleJsonObject.getString("name")
                val role = Role(roleId, roleName)
                roles.add(role)
            }

            return User(id, name, surname, email, firstLogin, roles)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        return Base64.decode(strEncoded, Base64.URL_SAFE).decodeToString()
    }
}

