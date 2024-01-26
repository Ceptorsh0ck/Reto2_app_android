package com.example.reto2_app_android.data.model

import android.os.Parcelable
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import kotlinx.android.parcel.Parcelize
import java.sql.Date


@Parcelize
data class ChatResponse_Message(
    val id: Int,
    val dataType: RoomDataType?,
    val content: String,
    val createdAt: java.util.Date?,
    var userId: ChatResponse_UserOfMessage?

): Parcelable