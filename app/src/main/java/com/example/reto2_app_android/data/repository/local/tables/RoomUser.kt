package com.example.reto2_app_android.data.repository.local.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ])
data class RoomUser(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "id_server") val idServer: Int? = null,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname1") val surname1: String?,
    @ColumnInfo(name = "surname2") val surname2: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "phone_number1") val phoneNumber1: Int?,
    @ColumnInfo(name = "phone_number2") val phoneNumber2: Int?,
    @ColumnInfo(name = "firstLogin") val firstLogin: Boolean?,
    )
