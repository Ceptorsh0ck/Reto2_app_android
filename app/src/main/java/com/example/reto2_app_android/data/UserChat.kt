package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserChat(
    val id: Int,
    val name: String,
    val isAdmin: Boolean
): Parcelable