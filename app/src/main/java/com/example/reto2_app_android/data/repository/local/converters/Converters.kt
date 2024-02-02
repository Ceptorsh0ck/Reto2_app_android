package com.example.reto2_app_android.data.repository.local.converters

import androidx.room.TypeConverter
import java.sql.Blob
import java.sql.SQLException
import java.util.Date
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.sql.*;

class Converters {

    @TypeConverter
    fun fromDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toDate(value: Date?): Long? {
        return value?.time
    }

}