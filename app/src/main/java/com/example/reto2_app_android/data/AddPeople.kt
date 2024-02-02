package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPeople (
    val userId: Int,
    val email: String,
    val isAdmin: Boolean = false,
): Parcelable