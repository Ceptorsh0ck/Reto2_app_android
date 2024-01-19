package com.example.reto2_app_android.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatResponse_UserMessage(
    val id: ChatResponse_ForeignKeysFromChat,
    val user: ChatResponse_UserFromUserList,
    val admin: Boolean
): Parcelable