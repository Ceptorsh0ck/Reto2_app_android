package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "user_chat",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomChat::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["user_id", "chat_id"]
)
data class RoomUserChat (
    @ColumnInfo(name ="user_id") val userId: Int,
    @ColumnInfo(name ="chat_id") val chatId: Int,
    @ColumnInfo(name ="is_admin") val isAdmin: Boolean,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?
)