package com.example.socketapp.data.socket

enum class SocketEvents(val value: String) {
    ON_MESSAGE_RECEIVED("chat message"),
    ON_SEND_MESSAGE("chat message"),
    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),
    ON_SEND_ID_MESSAGE("chat message id"),
    ON_DISCONECT_USER("user disconet"),
}
