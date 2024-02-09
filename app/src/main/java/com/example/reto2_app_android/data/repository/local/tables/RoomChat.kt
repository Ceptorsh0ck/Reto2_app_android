package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.reto2_app_android.data.repository.local.converters.Converters
import java.util.Date


@Entity(tableName = "chats")
data class RoomChat (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "id_server") val idServer: Int? = null,
    @ColumnInfo(name ="name") val name: String?,
    @ColumnInfo(name ="is_Public") var isPublic: Boolean,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?

    /* @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
     @TypeConverters(Converters::class) // Puedes necesitar esta anotación si Room no maneja directamente Date
     val createdAt: Date? = null,
     @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
     @TypeConverters(Converters::class) // Puedes necesitar esta anotación si Room no maneja directamente Date
     val updatedAt: Date? = null*/
)