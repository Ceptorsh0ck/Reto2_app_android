package com.example.socketapp.data.socket

import java.util.Date

data class SocketMessageRes (
    val id: Int,
    val messageType: MessageType,
    val room: String,
    val message: String,
    val authorName: String,
    val authorId: Integer,
    val fecha: String?,
    val hora: String?,
    val createdAt: Date? = null
)
