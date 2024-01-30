package com.example.reto2_app_android.data.repository.local

import android.util.Log
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.ChatResponse_Message
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.model.ChatResponse_UserOfMessage
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.data.repository.local.dao.ChatDao
import com.example.reto2_app_android.data.repository.local.dao.MessageDao
import com.example.reto2_app_android.data.repository.local.dao.UserDao
import com.example.reto2_app_android.utils.Resource

class RoomChatDataSource: CommonChatRepository {
    private val chatDao: ChatDao = MyApp.db.chatDao()
    private val messageDao: MessageDao = MyApp.db.messageDao()
    private val userDao: UserDao = MyApp.db.userDao()
    private val userId: Int? = MyApp.userPreferences.getLoggedUser()?.id?.toInt()

    override suspend fun getChats(): Resource<List<ChatResponse_Chat>> {
        if (userId != null) {
            val response = chatDao.getChatsByUserId(userId)
            val user = ChatResponse_User()
            Log.i("RoomChatDataSource", response.toString());
            user.listChats = emptyList()
            if (response != null) {
                response.forEach {
                    val mesage = messageDao.getLastMessageByRoomId(it.id)
                    val chatMessageList = mutableListOf<ChatResponse_Message>()
                    val chatMessage: ChatResponse_Message? = mesage?.let { it1 ->
                        mesage.content?.let { it2 ->
                            ChatResponse_Message(
                                it1.id,
                                mesage.dataType,
                                it2,
                                mesage.createdAt,
                                mesage.updatedAt,
                                null
                            )
                        }
                    }

                    if (chatMessage != null) {

                        val userFromRoom = userDao.selectUserOfMessage(mesage.userId)
                        val chatMessageUser =ChatResponse_UserOfMessage(
                            id = userFromRoom.id,
                            email = userFromRoom.email,
                            name = userFromRoom.name,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                        Log.i("messages change", chatMessageUser.toString())
                        chatMessage.userId = chatMessageUser
                    } else {
                        // Manejar el caso cuando chatMessage es nulo
                    }
                    chatMessage?.let { chatMessageList.add(it) } // Agregar a la lista solo si chatMessage no es nulo

                    val chat = ChatResponse_Chat(
                        it.id,
                        it.name,
                        it.createdAt,
                        it.updatedAt,
                        chatMessageList,
                        null,
                        it.isPublic
                    )

                    Log.i("RoomChatDataSour1ce", chat.toString())
                    user.listChats = user.listChats?.plus(chat)
                }

                return Resource.success(user.listChats!!)
            } else {
                return Resource.error("Response is null")
            }
        } else {
            return Resource.error("User id is null")
        }
    }



}