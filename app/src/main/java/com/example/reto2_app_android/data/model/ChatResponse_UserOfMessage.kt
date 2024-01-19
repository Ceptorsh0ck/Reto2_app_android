package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatResponse_UserOfMessage(
    val id: Int?,
    val email: String?,
    val name: String?,
    val surname1: String?,
    val surname2: String?,
    val phoneNumber1: Int?,
    val enabled: Boolean?,
    val accountNonLocked : Boolean?,
    val accountNonExpired: Boolean?,
    val credentialsNonExpired: Boolean?,
    val username: String?,
): Parcelable