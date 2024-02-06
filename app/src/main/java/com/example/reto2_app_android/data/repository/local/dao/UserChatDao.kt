package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import retrofit2.http.DELETE

@Dao
interface UserChatDao {
    /*@Insert
    suspend fun insertUserChat(userChat: RoomUserChat)

    @Query("SELECT * FROM user_chat WHERE messageId = :userChatId")
    suspend fun getUserChatById(userChatId: Int): RoomUserChat?*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserChat(userChat: RoomUserChat)

    @Query("DELETE FROM user_chat WHERE chat_id = :chatId AND user_id = :userId")
    suspend fun deleteUserChat(chatId: Int, userId: Int)

    @Query("SELECT user_chat.is_admin FROM user_chat WHERE chat_id = :chatId AND user_id = :userId")
    suspend fun isAdmin(chatId: Int, userId: Int?): Boolean

    @Query("Select count(user_id) from user_chat where chat_id = :id ")
    suspend fun getTotalUsers(id: Int): Int?

}