package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.socketapp.data.socket.SocketMessageReq

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(user: RoomMessages): Long

    @Query("SELECT * FROM messages WHERE chat_id = :chatId order by id desc limit 1")
    suspend fun getLastMessageByRoomId(chatId: Int): RoomMessages?

    @Query("SELECT messages.chat_Id as room, messages.content as text, users.email as authorName, messages.user_id as authorId, messages.data_type as dataType, messages.created_At as createdAt, messages.id as idRoom FROM messages LEFT JOIN users ON messages.user_id = users.id WHERE chat_id = :chatId ORDER BY messages.id")
    suspend fun getAllMessagesByChatId(chatId: Int): List<MessageAdapter>

    @Query("UPDATE messages SET id_server = :idServer, recived = true  WHERE id = :idRoom")
    suspend fun updateMessage(idRoom: Int, idServer: Int)

    @Query("SELECT messages.chat_Id as room, messages.content as text, users.email as authorName, messages.user_id as authorId, messages.data_type as dataType, messages.created_At as createdAt, messages.id as idRoom FROM messages LEFT JOIN users ON messages.user_id = users.id WHERE messages.id = :messageId")
    suspend fun getMessageById(messageId: Int): List<MessageAdapter>

    @Query("SELECT messages.id from messages where messages.id_server = :idServer")
    suspend fun selectById(idServer: Int?): Int

    @Query("SELECT messages.chat_id as room, messages.content as message, messages.id as idRoom, messages.data_type as type from messages where messages.id_server IS NULL")
    suspend fun getMessagesNoSended(): List<SocketMessageReq>
}