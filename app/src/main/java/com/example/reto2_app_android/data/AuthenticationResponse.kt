package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthenticationResponse(
    val email: String,
    val accessToken: String
): Parcelable