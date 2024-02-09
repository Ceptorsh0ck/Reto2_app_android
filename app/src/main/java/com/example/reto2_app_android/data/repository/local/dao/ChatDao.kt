package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import retrofit2.http.DELETE


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
   /* @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat1(chat: RoomChat): Long
*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChats(chat: RoomChat): Long

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

    @Query("Update chats set id_Server = :idServer where id = :idRoom")
    suspend fun updateChat(idServer: Int, idRoom: Int): Int

    @Query("Delete from chats where id = :id")
    suspend fun deleteChat(id: Int)
    @Query("SELECT is_Public from chats where id = :id")
    suspend fun getIsPublic(id: Int): Boolean


}