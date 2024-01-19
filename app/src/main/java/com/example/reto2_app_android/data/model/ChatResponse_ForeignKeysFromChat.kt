package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ChatResponse_ForeignKeysFromChat(
    val chatId: Int,
    val userId: Int
): Parcelable