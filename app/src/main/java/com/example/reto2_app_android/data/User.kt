package com.example.reto2_app_android.data

import android.os.Parcelable
import com.example.reto2_app_android.data.model.Role
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int?,
    val name: String?,
    val surname: String?,
    val surname2: String?,
    val dni: String?,
    val phone1: Int?,
    val phone2: Int?,
    val email: String?,
    val firstLogin: Boolean?,
    val listRoles: List<Role>,
): Parcelable