package com.example.reto2_app_android.data

import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import java.util.Date

data class MessageAdapter (
    val room: String,
    val text: String,
    val authorName: String,
    val authorId: Int?,
    val dataType: RoomDataType,
    val fecha: String?,
    val hora: String?,
    val createdAt: Date? = null,
    val idRoom: Int? = null
    )