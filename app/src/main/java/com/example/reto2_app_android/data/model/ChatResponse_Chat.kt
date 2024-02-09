package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class ChatResponse_Chat(
    var id: Int,
    val name: String?,
    var createdAt: Date?,
    var updatedAt: Date?,
    val listMessages: List<ChatResponse_Message>?,
    val listUsers: List<ChatResponse_UserMessage>?,
    val aIsPublic: Boolean,
    val totalUsers: Int? = null,
    var idRoom: Int? = null,
): Parcelable
