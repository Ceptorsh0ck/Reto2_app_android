package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages

@Dao
interface MessageDao {
    /*@Insert
    suspend fun insertMessage(message: RoomMessages)

    @Query("SELECT * FROM messages WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: Int): RoomMessages?*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(user: RoomMessages)
}