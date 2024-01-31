package com.example.reto2_app_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatResponse_User(
    val id: Int?,
    val email: String?,
    val name: String?,
    val surname1: String?,
    val surname2: String?,
    val address: String?,
    val phoneNumber1: Int?,
    val phoneNumber2: Int?,
    val firstLogin: Boolean?,
    val enabled: Boolean?,
    val accountNonLocked : Boolean?,
    val accountNonExpired: Boolean?,
    val credentialsNonExpired: Boolean?,
    val username: String?,
    val dni: String?,
    val roles: List<ChatResponse_Rol>?,
    var listChats: List<ChatResponse_Chat>?
): Parcelable{
    constructor(id: Int?,
                email: String?,
                name: String?,
                surname1: String?,
                surname2: String?,
                address: String?,
                phoneNumber1: Int?,
                enabled: Boolean?,
                accountNonLocked: Boolean?,
                accountNonExpired: Boolean?,
                credentialsNonExpired: Boolean?,
                username: String?) :
            this(
                    id,
                    email,
                    name,
                    surname1,
                    surname2,
                    address,
                    phoneNumber1,
                null,
            null,
                    enabled,
                    accountNonLocked,
                    accountNonExpired,
                    credentialsNonExpired,
                    username,
                null,
               null,
            null
                )
    constructor(
        id: Int?,
        name: String?,
        email: String?) :this(
        id,
        email,
        name,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
    constructor(): this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}