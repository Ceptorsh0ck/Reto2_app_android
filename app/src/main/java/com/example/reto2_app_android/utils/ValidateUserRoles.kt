package com.example.reto2_app_android.utils

import com.example.reto2_app_android.data.model.Role
import com.example.reto2_app_android.data.model.RoleEnum

class ValidateUserRoles {
    fun validateUserRoles(userRoles: List<Role>, validUser: List<RoleEnum>): Boolean {
        for (role in userRoles) {
            for(reles in validUser){
                if (role.name == reles.name) {
                    return true
                }
            }

        }
        return false
    }

}