package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPeople (
    val id: Int,
    val mail: String,
    val isAdmin: Boolean
): Parcelable