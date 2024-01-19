package com.example.reto2_app_android.data.model

import android.os.Parcelable
import com.example.reto2_app_android.data.DataType
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import kotlinx.android.parcel.Parcelize
import java.sql.Blob
import java.sql.Date


@Parcelize
data class ChatResponse_Message(
    val id: Int,
    val dataType: RoomDataType,
    val content: ByteArray?,
    val createdAt: Date?,
    val userId: ChatResponse_UserOfMessage

): Parcelable