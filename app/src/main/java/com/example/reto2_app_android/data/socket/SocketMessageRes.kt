package com.example.socketapp.data.socket

data class SocketMessageRes (
    val id: Int,
    val messageType: MessageType,
    val room: String,
    val message: String,
    val authorName: String,
    val authorId: Integer,
    val fecha: String?,
    val hora: String?,
)
