package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.Date


@Entity(tableName = "role_users",
    foreignKeys = [
        ForeignKey(
            entity = RoomRole::class,
            parentColumns = ["id"],
            childColumns = ["role_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["role_id", "user_id"]
)
data class RoomUserRol (
    @ColumnInfo(name ="role_id") val roleId: Int,
    @ColumnInfo(name ="user_id") val userId: Int,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?
)