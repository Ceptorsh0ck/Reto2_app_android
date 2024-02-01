package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatResponese_NewChat(
    val name: String,
    val isPublic: Boolean
): Parcelable