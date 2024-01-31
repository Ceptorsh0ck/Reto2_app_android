package com.example.reto2_app_android

import com.google.gson.annotations.SerializedName

data class NotificationData(@SerializedName("CustomKey") val customKey: String,
                            @SerializedName("anotherkey") val anotherKey: String)
