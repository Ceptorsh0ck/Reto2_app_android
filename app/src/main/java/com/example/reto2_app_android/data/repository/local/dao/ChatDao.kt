package com.example.reto2_app_android.data.repository.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import com.example.reto2_app_android.data.repository.local.tables.RoomChatWithDetails
import com.example.reto2_app_android.data.repository.local.tables.RoomMessageWithUserDetails
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChatWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    /*@Query("SELECT * FROM user_chat WHERE user_id = :userId")
    fun getUserChats(userId: Int): Flow<List<RoomUserChatWithDetails>>

    // ...

    @Transaction
    @Query("SELECT * FROM chats WHERE messageId = :chatId")
    fun getChatWithDetails(chatId: Int): Flow<RoomChatWithDetails>?

    // ...

    @Transaction
    @Query("SELECT * FROM messages WHERE chatId = :chatId")
    fun getMessagesWithDetails(chatId: Int): Flow<List<RoomMessageWithUserDetails>>*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChat(user: RoomChat)
}