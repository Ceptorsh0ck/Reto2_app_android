package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatShow (
    val chatId: Int,
    val name: String
): Parcelable