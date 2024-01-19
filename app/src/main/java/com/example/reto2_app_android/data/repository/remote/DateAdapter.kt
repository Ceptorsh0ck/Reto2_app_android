package com.example.reto2_app_android.data.repository.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {
    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

        @FromJson
        fun fromJson(dateString: String?): Date? {
            if (dateString.isNullOrEmpty()) {
                return null
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            return try {
                dateFormat.parse(dateString)
            } catch (e: ParseException) {
                null
            }
        }

        @ToJson
        fun toJson(date: Date?): String? {
            if (date == null) {
                return null
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            return dateFormat.format(date)
        }
    }
}