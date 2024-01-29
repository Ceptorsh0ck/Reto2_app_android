package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserNew(
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
    constructor() : this("", "", "", "", "", "", 0, 0, "")

    // Constructor secundario solo para las contraseñas
    constructor(
        oldPassword: String,
        newPassword: String
    ) : this(oldPassword, newPassword, "", "", "", "", 0, 0, "")

    // Constructor secundario para todo menos las contraseñas
    constructor(
        newPassword: String,
        name: String,
        surname1: String,
        surname2: String,
        DNI: String,
        telephone1: Int,
        telephone2: Int,
        photo: String
    ) : this("", newPassword, name, surname1, surname2, DNI, telephone1, telephone2, photo)
}
