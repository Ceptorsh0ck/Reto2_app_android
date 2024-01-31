package com.example.reto2_app_android.data.repository.local

import android.util.Log
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.dao.MessageDao
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes

class RoomMessageDataSource: CommonMessageRepository {
    private val messageDao: MessageDao = MyApp.db.messageDao()
    override suspend fun insertMessage(message: RoomMessages): Resource<List<MessageAdapter>> {
        Log.d("insert", message.toString())
        if(messageDao.selectById(message.idServer) == null) {
            val insertResult = messageDao.insertMessage(message)
            return Resource.success(messageDao.getMessageById(insertResult.toInt()))  // Assuming you want to return Int
        }
        return Resource.error("El message ya existe en base de datos room")

    }

    override suspend fun getAllMessagesById(idUser: Int): Resource<List<MessageAdapter>> {
        val allMessages = messageDao.getAllMessagesByChatId(idUser)
        return Resource.success(allMessages)
    }

    override suspend fun updateMessage(message: SocketMessageResUpdate): Resource<List<MessageAdapter>> {
        messageDao.updateMessage(message.idRoom, message.idServer)
        return Resource.success(messageDao.getMessageById(message.idRoom))

    }

    override suspend fun getMessagesNoSended(): Resource<List<SocketMessageReq>> {

        Log.i("Reconezion", "funcion2")
        val listMessages = messageDao.getMessagesNoSended();

        Log.i("Reconezion", listMessages.toString())
        return Resource.success(listMessages)
    }

    override suspend fun getAllUsersToInsertIntoChat(idChat: Int): Resource<List<AddPeople>> {
        return Resource.error("error")
    }

    override suspend fun addUsersToChats(idChat: Int, list: List<AddPeopleResponse>) {

    }


}