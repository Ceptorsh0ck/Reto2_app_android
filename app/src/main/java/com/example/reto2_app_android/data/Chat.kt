package com.example.reto2_app_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val public: Boolean,
    val name: String,
    val messages: Boolean,
    val users: List<UserChat>,
)