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

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected

    private val _message = MutableLiveData<Resource<Int>>()

    val message : MutableLiveData<Resource<Int>> get() = _message

    private val _messagesRoom = MutableLiveData<Resource<List<MessageAdapter>>>()

    val messagesRoom : MutableLiveData<Resource<List<MessageAdapter>>> get() = _messagesRoom

    private val _updateMessage = MutableLiveData<Resource<List<MessageAdapter>>>()
    val updateMessage : MutableLiveData<Resource<List<MessageAdapter>>> get() = _updateMessage


    private val SOCKET_HOST = "http://10.5.7.80:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"

    private lateinit var mSocket: Socket

    // TODO esto esta hardcodeeado
    private val SOCKET_ROOM = "default-room"


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

    private suspend fun connect() {
        withContext(Dispatchers.IO) {
            mSocket.connect();
        }
    }

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()

        // Add custom headers
        val headers = mutableMapOf<String, MutableList<String>>()
        headers[AUTHORIZATION_HEADER] = mutableListOf("Bearer ${MyApp.userPreferences.fetchAuthToken()}")

        options.extraHeaders = headers
        return options
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.d(TAG, "conectado")

            // no vale poner value por que da error al estar en otro hilo
            // IllegalStateException: Cannot invoke setValue on a background thread
            // en funcion asincrona obligado post
            _connected.postValue(Resource.success(true))
        }
    }
    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.d(TAG, "desconectado")
            _connected.postValue(Resource.success(false))
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener {
            // en teoria deberia ser siempre jsonObject, obviamente si siempre lo gestionamos asi
            if (it[0] is JSONObject) {
                Log.d(TAG, "mensaje recibido on new message ${it[0]}")
                //onNewMessageJsonObject(it[0])
            } else if (it[0] is String) {
                onNewMessageString(it[0])
            }
        }
    }

    fun onReciveMessageId(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("id recividas", "ids recividas ${it[0]}")
            if (it[0] is JSONObject) {
                //onUpdateMessageJsonObject(it[0])
                //onNewMessageJsonObject(it[0])
            } else if (it[0] is String) {
                //onNewMessageString(it[0])
            }

        }
    }

    fun onNewMessageString(data: Any) {
        try {
            // Manejar el mensaje recibido
            val message = data as String
            Log.d(TAG, "mensaje recibido $message")
            // ojo aqui no estoy actualizando la lista. aunque no deberiamos recibir strings
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }
    fun onUpdateMessageJsonObject(message: SocketMessageResUpdate) {
        try {

            viewModelScope.launch {
                val roomResponse = updateMessageInRomm(message)
                _messagesRoom.value = roomResponse
            }

        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }


    fun onNewMessageJsonObject(message : SocketMessageRes) {
        try {
            Log.i(TAG, message.authorName)

            Log.i(TAG, message.messageType.toString())
            val roomMessage = RoomMessages(
                idServer = message.id,
                content = message.message,
                dataType = RoomDataType.TEXT,
                createdAt = Date(),
                updatedAt = Date(),
                chatId = message.room.substring(message.room.length - 1, message.room.length).toInt(),
                userId = message.authorId.toInt(),
                recived = null
            )
            Log.i(TAG, roomMessage.toString())

            viewModelScope.launch {
                safeMessageInRomm(roomMessage)
            }
            updateMessageListWithNewMessage(message)
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }

    private fun updateMessageListWithNewMessage(message: SocketMessageRes) {
        try {
            val incomingMessage = MessageAdapter(message.room, message.message, message.authorName, message.authorId.toInt(), RoomDataType.TEXT, null, null)
            val msgsList = _messages.value?.data?.toMutableList()
            if (msgsList != null) {
                msgsList.add(incomingMessage)
                _messages.postValue(Resource.success(msgsList))
            } else {
                _messages.postValue(Resource.success(listOf(incomingMessage)))
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        }
    }


    fun onSaveMessage(message: String, socketRoom: String, idServer: Int){
        val socketMessage = SocketMessageReq(socketRoom, message, idServer)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
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


    suspend fun safeMessageInRomm(message: RoomMessages): Resource<Int> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.insertMessage(message)
        }
    }

    suspend fun updateMessageInRomm(message: SocketMessageResUpdate): Resource<List<MessageAdapter>> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.updateMessage(message)
        }
    }


}