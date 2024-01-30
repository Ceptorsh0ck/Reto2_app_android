package com.example.reto2_app_android.data.services
import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.ui.dashboard.DashboardViewModel
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketEvents
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.Date

class SocketIoService : Service() {
    private val serviceInitializedLiveData = MutableLiveData<Boolean>()
    private val channelId = "elorchat_channel"
    private val notificationId = 1
    private lateinit var serviceScope: CoroutineScope
    private val mBinder: IBinder = LocalService()
    private var messageQueue = mutableListOf<SocketMessageReq>()


    private val TAG = "SocketIoService"
    private lateinit var mSocket: Socket

    private val SOCKET_HOST = "http://10.5.7.23:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected

    private val _messagesFromOtherServer = MutableLiveData<Resource<SocketMessageRes>>()
    val messagesFromOtherServer : LiveData<Resource<SocketMessageRes>> get() = _messagesFromOtherServer

    private val _messagesFromMeServer = MutableLiveData<Resource<SocketMessageResUpdate>>()
    val messagesFromMeServer : LiveData<Resource<SocketMessageResUpdate>> get() = _messagesFromMeServer

    private val roomMessageRepository = RoomMessageDataSource();

    // TODO esto esta hardcodeeado
    private val SOCKET_ROOM = "default-room"

    inner class LocalService : Binder() {
        val service: SocketIoService
            get() = this@SocketIoService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main)
        createNotificationChannel()
        serviceInitializedLiveData.postValue(true)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ElorChat",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val context = this
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("ElorChat")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.elorrieta_logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notification = createNotification(contentText)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("services", "onStartCommand")
        val contentText = "ElorChat"
        startForeground(notificationId, createNotification(contentText))
        startSocket()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }






    private fun startSocket() {
        val socketOptions = createSocketOptions()
        mSocket = IO.socket(SOCKET_HOST, socketOptions)

        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        mSocket.on(SocketEvents.ON_SEND_ID_MESSAGE.value, onReciveMessageId())
        mSocket.on(SocketEvents.ON_DISCONECT_USER.value, onUserDisconnect())
        // usuari odesconectado

        mSocket.connect()
    }

    private fun onUserDisconnect(): Emitter.Listener? {
        return Emitter.Listener {
            Log.d("Socket", "disconect")
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
            Log.d("Socket", "conectado")
            _connected.postValue(Resource.success(true))
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Socket", "desconectado")
            _connected.postValue(Resource.success(false))
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if (receivedMessage is JSONObject) {
                Log.d("Socket", "mensaje recibido on new message ${receivedMessage}")
                val jsonObjectString = receivedMessage.toString()
                val message = Gson().fromJson(jsonObjectString, SocketMessageRes::class.java)
                // Modifica el valor del LiveData en el hilo principal utilizando postValue()
                saveNewMessageRoom(message)
                // _messagesFromOtherServer.postValue(Resource.success(message))
            }
        }
    }


    fun saveNewMessageRoom(message: SocketMessageRes) {
        // message: String, socketRoom: Int, userId: Int
        try {
            val roomId =  message.room.replace("Group- ", "").toInt()

            val roomMessage = RoomMessages(
                content = message.message,
                dataType = RoomDataType.TEXT,
                createdAt = Date(),
                updatedAt = Date(),
                chatId = roomId,
                userId = message.authorId.toInt(),
                recived = null,
                idServer = message.id
            )

            val incomingMessage = MessageAdapter(roomId.toString(), message.message, message.authorName, message.authorId.toInt(), RoomDataType.TEXT, null, null)


            serviceScope.launch {
                if (roomMessage != null) {
                    val roomResponse = safeMessageInRomm(roomMessage)
                    // roomMessage.id = roomResponse.toInt()
                    EventBus.getDefault().post(roomMessage)
                    // _message.value = roomResponse
                    EventBus.getDefault().post(incomingMessage)

                }
            }
        } catch (e: Exception) {
            Log.e("Socket", "error en recepcion de msg $message.toString()")
        }

    }


    private suspend fun safeMessageInRomm(message: RoomMessages): Resource<Int> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.insertMessage(message)
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

    private fun onReciveMessageId(): Emitter.Listener {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if (receivedMessage is JSONObject) {
                val jsonObjectString = receivedMessage.toString()
                val message = Gson().fromJson(jsonObjectString, SocketMessageResUpdate::class.java)
                serviceScope.launch {
                    if (message != null) {
                        val roomResponse = roomMessageRepository.updateMessage(message)
                        EventBus.getDefault().post(roomResponse.data)
                    }
                }
            }
        }
    }
/*
    fun addToMessageQueue(message: RoomMessages) {
        messageQueue.add(message)
    }

    private fun sendPendingMessages() {
        for (message in messageQueue) {
            onSaveMessage(message.content,  "Group- " + message.chatId, message.id)
        }
        messageQueue.clear() // Limpiar la cola después de enviar los mensajes
    }
*/
    fun onSaveMessage(message: String, socketRoom: String, idServer: Int){
        val socketMessage = SocketMessageReq(socketRoom, message, idServer)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
    }
/*
    fun sendMessagesWhenConnectIsBack() {
        Log.i("Reconezion", "funcion1")
        serviceScope.launch {
            val roomResponse = roomMessageRepository.getMessagesNoSended()
            Log.i("Reconezion2", roomResponse.data.toString())
            //messageQueue = roomResponse;

            roomResponse.data!!.forEach {
                onSaveMessage(it.message, it.room, it.idRoom)
            }
        }
    }*/
}