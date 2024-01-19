package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.Embedded
import androidx.room.Relation

data class RoomMessageWithUserDetails(
    @Embedded val message: RoomMessages,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "uid"
    )
    val user: RoomUser
)