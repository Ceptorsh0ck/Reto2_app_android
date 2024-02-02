package com.example.reto2_app_android.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserNew(
    var id: Int,
    var email: String,
    var oldPassword: String,
    var newPassword: String,
    var name: String,
    var surname1: String,
    var surname2: String,
    var dni: String,
    var firstLogin: Boolean,
    var phone1: Int,
    var phone2: Int,
    var photo: String
) : Parcelable {
    constructor(
        oldPassword: String,
        newPassword: String
    ) : this(0,"",oldPassword, newPassword, "", "", "", "",false, 0, 0, "")

    constructor(
        id: Int,
        email: String,
        newPassword: String,
        name: String,
        surname1: String,
        surname2: String,
        dni: String,
        firstLogin: Boolean,
        phone1: Int,
        phone2: Int,
        photo: String
    ) : this(id, email, "", newPassword, name, surname1, surname2, dni,firstLogin, phone1, phone2, photo)
    constructor() : this(0,"","", "", "", "", "", "",false, 0, 0, "")

}
