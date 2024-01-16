package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Blob

@Parcelize
data class UserNew(
    var email: String,
    var oldPassword: String,
    var newPassword: String,
    var name: String,
    var surname1: String,
    var surname2: String,
    var DNI: String,
    var telephone1: Int,
    var telephone2: Int,
    var photo: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", 0, 0, "")
}