package com.example.reto2_app_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import java.sql.Blob
import java.util.Date


@Entity(tableName ="messages")
data class Message (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val dataType: RoomDataType,
    val text: String,
    val creation: Date,
    val userId: Int,
    val chatId: Int

)