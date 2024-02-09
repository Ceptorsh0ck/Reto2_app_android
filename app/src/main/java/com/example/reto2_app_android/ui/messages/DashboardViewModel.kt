package com.example.reto2_app_android.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class DashboardViewModelFactory(
    private val roomMessageRepository: CommonMessageRepository,
    private val serverMessageRepository: CommonMessageRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return DashboardViewModel(roomMessageRepository, serverMessageRepository) as T
    }
}


class DashboardViewModel (
    private val roomMessageRepository: CommonMessageRepository,
    private val serverMessageRepository: CommonMessageRepository
) : ViewModel() {

    private val TAG = "SocketViewModel"

    private val _messages = MutableLiveData<Resource<List<MessageAdapter>>>()
    val messages: LiveData<Resource<List<MessageAdapter>>> get() = _messages


    private val _message = MutableLiveData<Resource<List<MessageAdapter>>>()

    val message: MutableLiveData<Resource<List<MessageAdapter>>> get() = _message

    private val _messagesRoom = MutableLiveData<Resource<List<MessageAdapter>>>()

    val messagesRoom: MutableLiveData<Resource<List<MessageAdapter>>> get() = _messagesRoom

    private val _users = MutableLiveData<Resource<List<AddPeople>>>()

    val users: MutableLiveData<Resource<List<AddPeople>>> get() = _users

    private val _usersDelete = MutableLiveData<Resource<List<AddPeople>>>()

    val usersDelete: MutableLiveData<Resource<List<AddPeople>>> get() = _usersDelete


    private val _updateMessage = MutableLiveData<Resource<List<MessageAdapter>>>()
    val updateMessage: MutableLiveData<Resource<List<MessageAdapter>>> get() = _updateMessage

    private val _admin = MutableLiveData<Resource<Boolean>>()
    val admin: MutableLiveData<Resource<Boolean>> get() = _admin

    fun getAllMessages(id: Int) {
        viewModelScope.launch {
            val roomResponse = getMessagesFromRoom(id)
            _messagesRoom.value = roomResponse
        }
    }

    fun getUsersToInsertIntoChats(idChat: Int) {
        viewModelScope.launch {
            val serverResponse = getUsersFromServer(idChat)
            _users.value = serverResponse;
        }
    }

    fun getUsersToDeleteIntoChats(idChat: Int) {
        viewModelScope.launch {
            val serverResponse = getUsersFromServerToDelete(idChat)
            _usersDelete.value = serverResponse
        }
    }

    fun deleteChat(id: Int) {
        viewModelScope.launch {
            try {
                deleteChatRoom(id)
            } catch (e: Exception) {
                // Manejar cualquier excepción que ocurra durante la eliminación del chat
            }
        }
    }

    private suspend fun deleteChatRoom(id: Int) {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.deleteChat(id)
        }
    }

    private suspend fun getUsersFromServer(idChat: Int): Resource<List<AddPeople>> {
        return withContext(Dispatchers.IO) {
            serverMessageRepository.getAllUsersToInsertIntoChat(idChat)
        }
    }

    private suspend fun getUsersFromServerToDelete(idChat: Int): Resource<List<AddPeople>> {
        return withContext(Dispatchers.IO) {
            serverMessageRepository.getAllUsersToDeleteIntoChat(idChat)
        }
    }


    suspend fun getMessagesFromRoom(id: Int): Resource<List<MessageAdapter>> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.getAllMessagesById(id);
        }
    }


    fun saveNewMessageRoom(message: String, socketRoom: Int, userId: Int, type: RoomDataType) {
        val roomMessage = RoomMessages(
            content = message,
            dataType = type,
            createdAt = Date(),
            updatedAt = Date(),
            chatId = socketRoom,
            userId = userId,
            recived = false
        )
        viewModelScope.launch {
            if (roomMessage != null) {
                val roomResponse = safeMessageInRomm(roomMessage)
                _message.value = roomResponse
            }
        }
    }


    private suspend fun safeMessageInRomm(message: RoomMessages): Resource<List<MessageAdapter>> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.insertMessage(message)
        }
    }

    fun isAdmin(chatId: Int, userId: Int?) {
        viewModelScope.launch {
            admin.value = isAdminRoom(chatId, userId)
        }
    }

    private suspend fun isAdminRoom(chatId: Int, userId: Int?): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.isAdmin(chatId, userId)
        }
    }


}