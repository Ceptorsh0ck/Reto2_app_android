package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users",
    indices = [Index(value = ["email"], unique = true)])
data class RoomUser(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname1") val surname1: String,
    @ColumnInfo(name = "surname2") val surname2: String,
    @ColumnInfo(name = "phone_number1") val phoneNumber1: Int
)
