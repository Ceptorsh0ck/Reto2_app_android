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
    @Query("SELECT id_server FROM users")
    suspend fun getIdServers(): List<Int>


    @Query("Select users.* from users where users.id = :userId")
    suspend fun selectUserOfMessage(userId: Int): RoomUser

    @Query("Select users.id from users where users.id_server = :idServer")
    suspend fun selectUserByServerId(idServer: Int?): Int



}