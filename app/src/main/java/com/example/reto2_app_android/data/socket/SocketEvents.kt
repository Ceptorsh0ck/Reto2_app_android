package com.example.socketapp.data.socket

enum class SocketEvents(val value: String) {
    ON_MESSAGE_RECEIVED("chat message"),
    ON_SEND_MESSAGE("chat message"),
    ON_CONNECT("connect"),
    ON_DISCONNECT("disconnect"),
    ON_SEND_ID_MESSAGE("chat message id"),
    ON_DISCONECT_USER("user disconet"),
    ON_ADD_USER_CHAT_SEND("add user chat send"),
    ON_ADD_USER_CHAT_RECIVE("add user chat recive"),
    ON_DELETE_USER_CHAT_SEND("delete user chat send"),
    ON_DELETE_USER_CHAT_RECIVE("delete user chat recive"),
    ON_CREATE_CHAT_SEND("create chat send"),
    ON_CREATE_CHAT_RECIVE("create chat recive"),
    ON_CREATE_CHAT_RECIVE_ID("create chat recive id"),
    ON_DELETE_CHAT_SEND("delete chat send"),
    ON_DELETE_CHAT_RECIVE("delete chat recive"),
}
