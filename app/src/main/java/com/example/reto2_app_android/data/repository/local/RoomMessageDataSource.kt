package com.example.reto2_app_android.data.repository.local

import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.dao.MessageDao
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.utils.Resource

class RoomMessageDataSource: CommonMessageRepository {
    private val messageDao: MessageDao = MyApp.db.messageDao()
    override suspend fun insertMessage(message: RoomMessages): Resource<Int> {
        val insertResult = messageDao.insertMessage(message)
        return Resource.success(insertResult.toInt())  // Assuming you want to return Int
    }

    override suspend fun getAllMessagesById(idUser: Int): Resource<List<RoomMessages>> {
        val allMessages = messageDao.getAllMessagesByChatId(idUser)
        return Resource.success(allMessages)
    }


}