package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CrateChat (
    val userId: Int,
    val name: String,
    val aIsPublic: Boolean,
    val roomChatId: Int
): Parcelable