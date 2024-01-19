package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.Embedded
import androidx.room.Relation

data class RoomChatWithDetails(
    @Embedded val chat: RoomChat,
    @Relation(
        parentColumn = "messageId",
        entityColumn = "chat_id"
    )
    val users: List<RoomUser>,
    @Relation(
        parentColumn = "messageId",
        entityColumn = "chat_id"
    )
    val messages: List<RoomMessageWithUserDetails>
)