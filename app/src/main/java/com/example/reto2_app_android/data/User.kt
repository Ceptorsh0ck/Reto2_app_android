package com.example.reto2_app_android.data

import android.os.Parcelable
import com.example.reto2_app_android.data.model.Role
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int?,
    val name: String?,
    val surname: String?,
    val email: String?,
    val firstLogin: Boolean?,
    val listRoles: List<Role>,
): Parcelable