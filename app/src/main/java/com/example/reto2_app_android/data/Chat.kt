package com.example.reto2_app_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="chats")
data class Chat (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val privacity: Boolean,
    val name: String,
    val messages: List<Message>,
    val users: List<UserChat>,
)