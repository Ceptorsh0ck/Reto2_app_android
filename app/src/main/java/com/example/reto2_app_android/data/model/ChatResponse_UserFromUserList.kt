package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ChatResponse_UserFromUserList(
    val id: Int?,
    val email: String?,
    val name: String?
): Parcelable