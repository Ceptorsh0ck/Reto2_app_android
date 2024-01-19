package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat

@Dao
interface UserChatDao {
    /*@Insert
    suspend fun insertUserChat(userChat: RoomUserChat)

    @Query("SELECT * FROM user_chat WHERE messageId = :userChatId")
    suspend fun getUserChatById(userChatId: Int): RoomUserChat?*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserChat(userChat: RoomUserChat)
}