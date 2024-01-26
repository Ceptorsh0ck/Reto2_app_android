package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import javax.sql.rowset.serial.SerialBlob

@Entity(tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = RoomChat::class,
            parentColumns = ["id_server"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["id_server"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_server"], unique = true)])
data class RoomMessages(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Autoincremental primary key
    @ColumnInfo(name = "id_server") val idServer: Int? = null,  // Unique and nullable
    @ColumnInfo(name ="content") val content: String,
    @ColumnInfo(name ="data_type") val dataType: RoomDataType?,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?,
    @ColumnInfo(name ="chat_id") val chatId: Int,
    @ColumnInfo(name ="user_id") val userId: Int
)
