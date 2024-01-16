package com.example.reto2_app_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob
import java.util.Date


@Entity(tableName ="messages")
data class Message (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val dataType: DataType,
    val text: Blob,
    val creation: Date,
    val userId: Int,
    val chatId: Int

)