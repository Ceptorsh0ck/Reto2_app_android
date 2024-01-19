package com.example.reto2_app_android.data.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import com.example.reto2_app_android.data.repository.local.tables.RoomUserRol

@Dao
interface UserRoleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserRole(userRol: RoomUserRol)
}