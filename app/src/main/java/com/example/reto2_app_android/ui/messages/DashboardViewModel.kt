package com.example.reto2_app_android.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.MyApp
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
    private val roomMessageRepository: CommonMessageRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return DashboardViewModel(roomMessageRepository) as T
    }
}


class DashboardViewModel (
    private val roomMessageRepository: CommonMessageRepository
) : ViewModel() {

    private val TAG = "SocketViewModel"

    private val _messages = MutableLiveData<Resource<List<MessageAdapter>>>()
    val messages : LiveData<Resource<List<MessageAdapter>>> get() = _messages


    private val _message = MutableLiveData<Resource<List<MessageAdapter>>>()

    val message : MutableLiveData<Resource<List<MessageAdapter>>> get() = _message

    private val _messagesRoom = MutableLiveData<Resource<List<MessageAdapter>>>()

    val messagesRoom : MutableLiveData<Resource<List<MessageAdapter>>> get() = _messagesRoom


    private val _updateMessage = MutableLiveData<Resource<List<MessageAdapter>>>()
    val updateMessage : MutableLiveData<Resource<List<MessageAdapter>>> get() = _updateMessage

  //TODO esto de aqui hay que borrar?
/*
    private val SOCKET_HOST = "http://10.5.7.80:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"

    private lateinit var mSocket: Socket

    // TODO esto esta hardcodeeado
    private val SOCKET_ROOM = "default-room"
    */


    /*fun startSocket() {
        val socketOptions = createSocketOptions();
        mSocket = IO.socket(SOCKET_HOST, socketOptions);

        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())

        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        mSocket.on(SocketEvents.ON_SEND_ID_MESSAGE.value, onReciveMessageId())
        viewModelScope.launch {
            connect()
        }
    }*/


    fun getAllMessages(id: Int) {
        viewModelScope.launch {
            val roomResponse = getMessagesFromRoom(id)
            _messagesRoom.value = roomResponse
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