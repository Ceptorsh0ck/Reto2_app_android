package com.example.reto2_app_android.data.repository.local.database

import com.example.reto2_app_android.data.repository.local.converters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reto2_app_android.data.repository.local.dao.ChatDao
import com.example.reto2_app_android.data.repository.local.dao.MessageDao
import com.example.reto2_app_android.data.repository.local.dao.RoleDao
import com.example.reto2_app_android.data.repository.local.dao.UserChatDao
import com.example.reto2_app_android.data.repository.local.dao.UserDao
import com.example.reto2_app_android.data.repository.local.dao.UserRoleDao
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.local.tables.RoomRole
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import com.example.reto2_app_android.data.repository.local.tables.RoomUserRol

@Database(
    entities = [RoomUser::class, RoomChat::class, RoomMessages::class, RoomUserChat::class, RoomUserRol::class, RoomRole::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun userChatDao(): UserChatDao
    abstract fun userRoleDao(): UserRoleDao
    abstract fun roleDao(): RoleDao
}
