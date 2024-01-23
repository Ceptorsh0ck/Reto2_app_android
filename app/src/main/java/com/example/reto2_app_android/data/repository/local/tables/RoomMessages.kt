package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.reto2_app_android.data.DataType
import java.util.Date
import javax.sql.rowset.serial.SerialBlob

@Entity(tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = RoomChat::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class RoomMessages(
    @PrimaryKey val id: Int,
    @ColumnInfo(name ="content") val content: ByteArray?,
    @ColumnInfo(name ="data_type") val dataType: RoomDataType?,
    @ColumnInfo(name ="created_at") val createdAt: Date?,
    @ColumnInfo(name ="updated_at") val updatedAt: Date?,
    @ColumnInfo(name ="chat_id") val chatId: Int,
    @ColumnInfo(name ="user_id") val userId: Int
)