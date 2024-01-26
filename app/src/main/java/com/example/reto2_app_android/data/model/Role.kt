package com.example.reto2_app_android.data.model

import android.os.Parcelable
import androidx.compose.ui.semantics.Role
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Role(
    val id: Int?,
    val name: String?,
): Parcelable