package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Date


@Parcelize
data class ChatResponse_Rol(
    val id: Int,
    val name: String
): Parcelable