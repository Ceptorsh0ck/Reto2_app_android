package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.socketapp.data.socket.SocketMessageRes

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(user: RoomMessages): Long

    @Query("SELECT * FROM messages WHERE chat_id = :chatId order by id desc limit 1")
    suspend fun getLastMessageByRoomId(chatId: Int): RoomMessages?

    @Query("SELECT messages.chat_Id as room, messages.content as text, users.email as authorName, messages.user_id as authorId, messages.data_type as dataType, messages.created_At as createdAt FROM messages LEFT JOIN users ON messages.user_id = users.id_server WHERE chat_id = :chatId ORDER BY messages.id")
    suspend fun getAllMessagesByChatId(chatId: Int): List<MessageAdapter>

    @Query("UPDATE messages SET id_server = :idServer, recived = true  WHERE id = :idRoom")
    suspend fun updateMessage(idRoom: Int, idServer: Int)

    @Query("SELECT messages.chat_Id as room, messages.content as text, users.email as authorName, messages.user_id as authorId, messages.data_type as dataType, messages.created_At as createdAt FROM messages LEFT JOIN users ON messages.user_id = users.id_server WHERE messages.id = :messageId")
    suspend fun getMessageById(messageId: Int): List<MessageAdapter>
}