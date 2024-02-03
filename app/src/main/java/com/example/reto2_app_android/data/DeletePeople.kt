package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeletePeople (
    val userId: Int,
    val chatId: Int?
): Parcelable