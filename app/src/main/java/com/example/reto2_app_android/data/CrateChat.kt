package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CrateChat (
    val userId: Int,
    val chatName: String,
    val isPublic: Boolean,
    val roomChatId: Int
): Parcelable