package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatResponse_Chat(
    val id: Int,
    val name: String,
    val listMessages: List<ChatResponse_Message>,
    val listUsers: List<ChatResponse_UserMessage>,
    val public: Boolean
): Parcelable