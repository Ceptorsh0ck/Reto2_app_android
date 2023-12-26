package com.example.reto2_app_android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="users")
data class DbUser (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val login: String,
    val password: String
)