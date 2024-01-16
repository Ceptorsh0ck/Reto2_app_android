package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLogin(
    val email: String,
    val password: String,
): Parcelable