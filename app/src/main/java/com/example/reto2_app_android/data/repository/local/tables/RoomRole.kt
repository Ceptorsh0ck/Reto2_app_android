package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "roles")
data class RoomRole (
    @PrimaryKey val id: Int,
    @ColumnInfo(name ="name") val name: String?,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?
)