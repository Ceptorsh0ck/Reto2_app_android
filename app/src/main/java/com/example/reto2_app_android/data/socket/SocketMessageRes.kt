package com.example.socketapp.data.socket

import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import java.util.Date

data class SocketMessageRes (
    val id: Int,
    val messageType: MessageType,
    val room: String,
    val message: String,
    val authorName: String,
    val authorId: Integer,
    val dataType: RoomDataType,
    val fecha: String?,
    val hora: String?,
    val createdAt: Date? = null
)
