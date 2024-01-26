package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "chats",
    indices = [Index(value = ["id_server"], unique = true)])
data class RoomChat (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_server") val idServer: Int? = null,
    @ColumnInfo(name ="name") val name: String?,
    @ColumnInfo(name ="is_Public") val isPublic: Boolean,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?
)