package com.example.reto2_app_android.data.repository.local

import android.util.Log
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.dao.ChatDao
import com.example.reto2_app_android.data.repository.local.dao.MessageDao
import com.example.reto2_app_android.data.repository.local.dao.UserChatDao
import com.example.reto2_app_android.data.repository.local.dao.UserDao
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes

class RoomMessageDataSource: CommonMessageRepository {
    private val messageDao: MessageDao = MyApp.db.messageDao()
    private val chatDao: ChatDao = MyApp.db.chatDao()
    private val userDao: UserDao = MyApp.db.userDao()
    private val userChatDao: UserChatDao = MyApp.db.userChatDao()
    override suspend fun insertMessage(message: RoomMessages): Resource<List<MessageAdapter>> {
        if(messageDao.selectById(message.idServer) == null) {
            var chatId: Int? = chatDao.selectChatByServerId(message.chatId)
            if(chatId == null) {
                chatId = message.chatId
            }
            var userId: Int? = userDao.selectUserByServerId(message.userId)
            if(userId == null) {
                userId = message.userId
            }
            var idServer:Int? = null;
            if(message.idServer != null){
                idServer = message.idServer
            }

            var message1 =RoomMessages(
                0,
                idServer = idServer,
                content = message.content,
                dataType = message.dataType,
                createdAt = message.createdAt,
                updatedAt = message.updatedAt,
                chatId = chatId,
                userId = userId,
                recived = false
            )

            val insertResult = messageDao.insertMessage(message1)
            return Resource.success(messageDao.getMessageById(insertResult.toInt()))  // Assuming you want to return Int
        }
        return Resource.error("El message ya existe en base de datos room")

    }



    override suspend fun getAllMessagesById(idChat: Int): Resource<List<MessageAdapter>> {
        val chatId = chatDao.selectChatByServerId(idChat)
        val allMessages = messageDao.getAllMessagesByChatId(chatId)
        return Resource.success(allMessages)
    }

    override suspend fun updateMessage(message: SocketMessageResUpdate): Resource<List<MessageAdapter>> {
        messageDao.updateMessage(message.idRoom, message.idServer)
        return Resource.success(messageDao.getMessageById(message.idRoom))

    }

    override suspend fun getMessagesNoSended(): Resource<List<SocketMessageReq>> {

        val listMessages = messageDao.getMessagesNoSended();

        return Resource.success(listMessages)
    }

    override suspend fun getAllUsersToInsertIntoChat(idChat: Int): Resource<List<AddPeople>> {
        return Resource.error("error")
    }

    override suspend fun getAllUsersToDeleteIntoChat(idChat: Int): Resource<List<AddPeople>> {
        TODO("Not yet implemented")
    }

    override suspend fun isAdmin(chatId: Int, userId: Int?): Resource<Boolean> {

        val idUser = userDao.selectUserByServerId(userId)
        val idChat = chatDao.selectChatByServerId(chatId)
        return Resource.success(userChatDao.isAdmin(idChat, idUser))
    }

    override suspend fun deleteChat(id: Int) {
        var chatRoom = chatDao.selectChatByServerId(id)
        chatDao.deleteChat(chatRoom)
    }


}