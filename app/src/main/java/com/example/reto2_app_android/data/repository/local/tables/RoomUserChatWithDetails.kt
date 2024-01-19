package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.Embedded
import androidx.room.Relation

data class RoomUserChatWithDetails(
    @Embedded val userChat: RoomUserChat,
    @Relation(
        parentColumn = "chat_id",
        entityColumn = "messageId"
    )
    val chat: RoomChat,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "uid"
    )
    val user: RoomUser
)