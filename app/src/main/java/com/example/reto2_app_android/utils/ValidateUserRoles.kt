package com.example.reto2_app_android.utils

import com.example.reto2_app_android.data.model.Role
import com.example.reto2_app_android.data.model.RoleEnum

class ValidateUserRoles {
    fun validateUserRoles(userRoles: List<Role>, validUser: List<RoleEnum>): Boolean {
        for (role in userRoles) {
            if (role.name == validUser[0].name || role.name == validUser[1].name) {
                return true
            }
        }
        return false
    }

}