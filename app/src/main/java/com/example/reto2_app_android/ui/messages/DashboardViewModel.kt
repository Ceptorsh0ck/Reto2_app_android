package com.example.reto2_app_android.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketEvents
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
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
    val messages : LiveData<Resource<List<MessageAdapter>>> get() = _messages


    private val _message = MutableLiveData<Resource<List<MessageAdapter>>>()

    val message : MutableLiveData<Resource<List<MessageAdapter>>> get() = _message

    private val _messagesRoom = MutableLiveData<Resource<List<MessageAdapter>>>()

    val messagesRoom : MutableLiveData<Resource<List<MessageAdapter>>> get() = _messagesRoom

    private val _users = MutableLiveData<Resource<List<AddPeople>>>()

    val users : MutableLiveData<Resource<List<AddPeople>>>get() = _users

    private val _addPeople = MutableLiveData<Resource<List<AddPeopleResponse>>>()

    val addPeople : MutableLiveData<Resource<List<AddPeopleResponse>>>get() = _addPeople


    fun getAllMessages(id: Int) {
        viewModelScope.launch {
            val roomResponse = getMessagesFromRoom(id)
            _messagesRoom.value = roomResponse
        }
    }

    fun getUsersToInsertIntoChats(idChat: Int){
        viewModelScope.launch {
            val serverResponse = getUsersFromServer(idChat)
            _users.value = serverResponse;
        }
    }

    fun updateChatUsers( idChat: Int, selectedPeopleList: MutableList<AddPeopleResponse>) {
        viewModelScope.launch {
            val list: List<AddPeopleResponse> = selectedPeopleList.toList()
            Log.d(TAG, list.toString())
            val serverResponse = serverAddUserToRoom(idChat, list)
            _addPeople.value = Resource.success(list)
        }
    }

    private suspend fun serverAddUserToRoom(idChat: Int, list: List<AddPeopleResponse>) {
        return withContext(Dispatchers.IO) {
            serverMessageRepository.addUsersToChats(idChat, list)
        }
    }

    private suspend fun getUsersFromServer(idChat: Int): Resource<List<AddPeople>> {
        return withContext(Dispatchers.IO) {
            serverMessageRepository.getAllUsersToInsertIntoChat(idChat)
        }
    }

    suspend fun getMessagesFromRoom(id: Int): Resource<List<MessageAdapter>> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.getAllMessagesById(id);
        }
    }

    fun saveNewMessageRoom(message: String, socketRoom: Int, userId: Int) {
        val roomMessage = RoomMessages(
            content = message,
            dataType = RoomDataType.TEXT,
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


}