package com.example.reto2_app_android.utils

import com.example.reto2_app_android.data.model.Role
import com.example.reto2_app_android.data.model.RoleEnum

class ValidateUserRoles {
    fun validateUserRoles(userRoles: List<Role>, validUser: RoleEnum): Boolean {
        for (role in userRoles) {
            if (role.name == validUser.name) {
                return true
            }
        }
        return false
    }
}