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
    suspend fun insertChat(user: RoomChat): Long

    @Query("SELECT chats.*\n" +
            "FROM chats\n" +
            "LEFT JOIN user_chat ON chats.id = user_chat.chat_id\n" +
            "LEFT JOIN users ON user_chat.user_id = users.id\n" +
            "LEFT JOIN (\n" +
            "    SELECT chat_id, MAX(created_at) AS max_created_at\n" +
            "    FROM messages\n" +
            "    GROUP BY chat_id\n" +
            ") AS last_message ON chats.id = last_message.chat_id\n" +
            "WHERE users.id_server = :userId\n" +
            "ORDER BY COALESCE(last_message.max_created_at, chats.created_at) DESC;\n" )
    suspend fun getChatsByUserId(userId: Int): List<RoomChat>

    @Query("SELECT id from chats where id_server = :idServer")
    suspend fun selectChatByServerId(idServer: Int?): Int


}