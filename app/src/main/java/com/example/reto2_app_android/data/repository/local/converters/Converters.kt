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

    @TypeConverter
    fun fromBlob(blob: Blob?): ByteArray? {
        return try {
            blob?.let {
                val inputStream = it.binaryStream
                val outputStream = ByteArrayOutputStream()
                var bytesRead: Int
                val buffer = ByteArray(4096) // Adjust the buffer size as needed
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
                outputStream.toByteArray()
            }
        } catch (e: SQLException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    /*@TypeConverter
    fun toBlob(byteArray: ByteArray?): Blob? {
        return byteArray?.let { javax.sql.serial.SerialBlob(it) }
    }*/
}