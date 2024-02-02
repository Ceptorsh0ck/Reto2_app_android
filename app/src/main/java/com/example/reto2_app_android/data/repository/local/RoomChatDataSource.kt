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
import com.example.reto2_app_android.data.repository.local.dao.UserChatDao
import com.example.reto2_app_android.data.repository.local.dao.UserDao
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import com.example.reto2_app_android.utils.Resource

class RoomChatDataSource: CommonChatRepository {
    private val chatDao: ChatDao = MyApp.db.chatDao()
    private val messageDao: MessageDao = MyApp.db.messageDao()
    private val userDao: UserDao = MyApp.db.userDao()
    private val userCharDao: UserChatDao = MyApp.db.userChatDao()
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

    override suspend fun addchat(chat: ChatResponse_Chat) {
            var roomId:Int? =1 ;
            //Log.i("chats", it!!.id.toString())
            val roomChat = RoomChat(
                    idServer = chat.id,
                    name = chat.name,
                    isPublic = chat.public,
                    createdAt = chat.createdAt,
                    updatedAt = chat.updatedAt
                )
            if (roomChat != null) {
                roomId = chatDao.selectChatByServerId(roomChat.idServer)
                if(roomId == null){
                    roomId = chatDao.insertChat(roomChat).toInt()
                }
            }

        chat.listUsers?.forEach {
            var userId:Int? = 1;
            val user = it.user.id?.let { it1 ->
                RoomUser(
                    idServer = it1,
                    email = it.user.email,
                    name = it.user.name,
                    surname1 = null,
                    surname2 = null,
                    address = null,
                    phoneNumber1 = null,
                    phoneNumber2 = null,
                    firstLogin = null
                )
            }
            if (user != null) {
                userId = userDao.selectUserByServerId(user.idServer)
                if(userId == null){
                    userId = userDao.insertUser(user).toInt()
                }
            }
            val userChat = it.user.id?.let { it1 ->
                user?.let { it3 ->
                    RoomUserChat(
                        userId = userId!!,
                        chatId = roomId!!,
                        isAdmin = it.admin,
                        createdAt = null,
                        updatedAt = null
                    )
                }
            }
            if (userChat != null) {
                userCharDao.insertUserChat(userChat)
            }
        }
        chat.listMessages?.forEach{
            val user = it.userId?.id?.let { it1 ->
                RoomUser(
                    idServer = it1,
                    name = it.userId!!.name,
                    email = it.userId!!.email,
                    surname1 = it.userId!!.surname1,
                    surname2 = it.userId!!.surname2,
                    address = null,
                    phoneNumber1 = it.userId!!.phoneNumber1,
                    phoneNumber2 = null,
                    firstLogin = null
                )
            }
            var userId:Int? = 1;
            if (user != null) {
                userId = userDao.selectUserByServerId(user.idServer)
                if(userId == null){
                    userId =userDao.insertUser(user).toInt()
                }
            }

            val message = RoomMessages(
                idServer = it.id,
                content = it.content,
                dataType = it.dataType,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                chatId = roomId!!,
                userId = userId!!,
                recived = null
            )
            if (message != null) {
                val idMessage:Int? = messageDao.selectById(message.idServer)
                if(idMessage == null){
                    messageDao.insertMessage(message)
                }
            }
        }
    }
}