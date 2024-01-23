package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reto2_app_android.data.repository.local.tables.RoomUser

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: RoomUser): Long

    @Query("Select users.* from users join messages on users.id = messages.user_Id where messages.id = :messageId")
    suspend fun selectUserOfMessage(messageId: Int): RoomUser
}