package com.example.socketapp.data.socket

import com.example.reto2_app_android.data.repository.local.tables.RoomDataType

data class SocketMessageReq(
    val room: String,
    val message: String,
    val idRoom: Int,
    val type: RoomDataType
)