package com.example.reto2_app_android.data.services
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.MyApp.Companion.context
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.CrateChat
import com.example.reto2_app_android.data.DeleteChat
import com.example.reto2_app_android.data.DeletePeople
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.local.RoomChatDataSource
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.RoomUserDataSource
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.socket.SocketMessageResUpdate
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.utils.MyHostnameVerifier
import com.example.reto2_app_android.utils.MyTrustManager
import com.example.reto2_app_android.utils.Resource
import com.example.socketapp.data.socket.SocketEvents
import com.example.socketapp.data.socket.SocketMessageReq
import com.example.socketapp.data.socket.SocketMessageRes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

class SocketIoService : Service() {
    private val serviceInitializedLiveData = MutableLiveData<Boolean>()
    private val channelId = "elorchat_channel"
    private val notificationId = 1
    private lateinit var serviceScope: CoroutineScope
    private val mBinder: IBinder = LocalService()
    private var messagesSended :Boolean = true

    private val TAG = "SocketIoService"
    private lateinit var mSocket: Socket
    private val userId: Int? = MyApp.userPreferences.getLoggedUser()?.id?.toInt()
    private val SOCKET_HOST = "https://10.0.2.2:8085/"
    private val AUTHORIZATION_HEADER = "Authorization"

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected
    private val roomUserRepository = RoomUserDataSource();
    private val roomMessageRepository = RoomMessageDataSource();
    private val chatMessageDataSource = RoomChatDataSource();
    // TODO esto esta hardcodeeado
    private val SOCKET_ROOM = "default-room"

    private val _items = MutableLiveData<Resource<List<ChatResponse_Chat>>>()

    val items: LiveData<Resource<List<ChatResponse_Chat>>> get() = _items

    private val _newChat = MutableLiveData<Resource<Int>>()

    val newChat: LiveData<Resource<Int>> get() = _newChat

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
        val contentText = "ElorChat"
        startForeground(notificationId, createNotification(contentText))
        startSocket()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mSocket.isInitialized && mSocket.connected()) {
            mSocket.disconnect()
            mSocket.off()
            mSocket.disconnect()
        }
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
        mSocket.on(SocketEvents.ON_ADD_USER_CHAT_RECIVE.value, onAddUserChatRecive())
        mSocket.on(SocketEvents.ON_DELETE_USER_CHAT_RECIVE.value, onDeleteUserChatRecive())
        mSocket.on(SocketEvents.ON_CREATE_CHAT_RECIVE.value, onCreateChat())
        mSocket.on(SocketEvents.ON_DELETE_CHAT_RECIVE.value, onDeleteChat())
        mSocket.connect()
    }

    private fun onDeleteChat(): Emitter.Listener? {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if (receivedMessage is JSONObject) {
                /*val jsonObjectString = receivedMessage.toString()
                val chat = Gson().fromJson(jsonObjectString, ChatResponse_Chat::class.java)
                chat.createdAt = Date()
                chat.updatedAt = Date()
                serviceScope.launch {
                    updateChat(chat)
                }*/

            }
        }
    }

    private fun onCreateChat(): Emitter.Listener? {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if (receivedMessage is JSONObject) {
                val jsonObjectString = receivedMessage.toString()
                val chat = Gson().fromJson(jsonObjectString, ChatResponse_Chat::class.java)
                chat.createdAt = Date()
                chat.updatedAt = Date()
                serviceScope.launch {
                    updateChat(chat)
                }

            }
        }
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
        options.secure = true
        val certificatesManager = MyTrustManager(context)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(certificatesManager.trustManager), null)
        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier(MyHostnameVerifier())
            .sslSocketFactory(sslContext.socketFactory, certificatesManager.trustManager)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
        options.callFactory = okHttpClient
        options.webSocketFactory = okHttpClient
        return options
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Socket", "conectado")
           //onSendMessagesWhenConnectionGoBack()
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Socket", "desconectado")
            messagesSended = true
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if (receivedMessage is JSONObject) {
                val jsonObjectString = receivedMessage.toString()
                val message = Gson().fromJson(jsonObjectString, SocketMessageRes::class.java)
                // Modifica el valor del LiveData en el hilo principal utilizando postValue()
                saveNewMessageRoom(message)
                // _messagesFromOtherServer.postValue(Resource.success(message))
            }
        }
    }

    private fun onDeleteUserChatRecive(): Emitter.Listener {
        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if(receivedMessage is JSONObject) {
                val jsonObjectString = receivedMessage.toString()
                val message = Gson().fromJson(jsonObjectString, AddPeopleResponse::class.java)
                serviceScope.launch {
                    deleteUserChatInRoom(message)
                    EventBus.getDefault().post(getChatsFromRoom())
                    if(userId == message.userId){
                        val userDeleted = DeletePeople(message.userId, message.chatId)
                        EventBus.getDefault().post(userDeleted)
                    }
                }
            }
        }
    }

    private fun onAddUserChatRecive(): Emitter.Listener? {
        // Definir el formato de fecha deseado
        val dateFormat = SimpleDateFormat("ddMMyyyyHHmmss")

        // Crear un deserializador personalizado para el tipo Date
        val dateDeserializer = object : JsonDeserializer<Date> {
            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
                val dateString = json.asString
                return dateFormat.parse(dateString)
            }
        }

        // Crear un GsonBuilder y configurarlo con el adaptador de fecha personalizado
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Date::class.java, dateDeserializer)
        val gson = gsonBuilder.create()

        return Emitter.Listener { args ->
            val receivedMessage = args[0]
            if(receivedMessage is JSONObject) {
                val jsonObjectString = receivedMessage.toString()
                val message = gson.fromJson(jsonObjectString, ChatResponse_Chat::class.java)
                serviceScope.launch {
                    safeChatInRoom(message)
                    EventBus.getDefault().post(getChatsFromRoom())
                }
            }
        }
    }

    suspend fun getChatsFromRoom(): Resource<List<ChatResponse_Chat>>{
        return  withContext(Dispatchers.IO){
            chatMessageDataSource.getChats()
        }
    }

    suspend fun updateChat(chat: ChatResponse_Chat) {
        return  withContext(Dispatchers.IO){
            chatMessageDataSource.updateChat(chat)
            EventBus.getDefault().post(true)
        }
    }

    fun saveNewMessageRoom(message: SocketMessageRes) {
        // message: String, socketRoom: Int, userId: Int
        try {
            val roomId =  message.room.replace("Group- ", "").toInt()

            val roomMessage = RoomMessages(
                content = message.message,
                dataType = message.dataType,
                createdAt = Date(),
                updatedAt = Date(),
                chatId = roomId,
                userId = message.authorId.toInt(),
                recived = null,
                idServer = message.id
            )

            val currentDate = SimpleDateFormat("dd/MM/yyyy").format(Date())
            val currentTime = SimpleDateFormat("HH:mm").format(Date())



            serviceScope.launch {
                if (roomMessage != null) {
                    val roomResponse = safeMessageInRomm(roomMessage)
                    val userIdRoom = getUserIdRoom(roomMessage.userId)
                    val roomMessageToSow = RoomMessages(
                        content = message.message,
                        dataType = message.dataType,
                        createdAt = Date(),
                        updatedAt = Date(),
                        chatId = roomId,
                        userId = userIdRoom.data!!,
                        recived = null,
                        idServer = message.id
                    )

                    val incomingMessage = MessageAdapter(
                        room = roomId.toString(),
                        text = message.message,
                        authorName = message.authorName,
                        authorId = userIdRoom.data,
                        dataType = message.dataType,
                        fecha = currentDate, // Agregar la fecha
                        hora = currentTime  // Agregar la hora
                    )


                    EventBus.getDefault().post(roomMessageToSow)
                    EventBus.getDefault().post(incomingMessage)

                }
            }
        } catch (e: Exception) {
        }

    }

    private suspend fun getUserIdRoom(userId: Int): Resource<Int> {
        return withContext(Dispatchers.IO) {
            roomUserRepository.getUserIdWithIdServer(userId)
        }
    }


    private suspend fun safeMessageInRomm(message: RoomMessages): Resource<List<MessageAdapter>> {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.insertMessage(message)
        }
    }
    private suspend fun safeChatInRoom(message: ChatResponse_Chat) {
        return withContext(Dispatchers.IO) {
            chatMessageDataSource.addchat(message)
        }
    }

    private suspend fun deleteUserChatInRoom(message: AddPeopleResponse) {
        return withContext(Dispatchers.IO) {
            chatMessageDataSource.deleteUserChat(message)
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
                        //EventBus.getDefault().post(roomResponse.data)
                    }
                }
            }
        }
    }

    private fun getBase64FromFile(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.let {
                val bytes = inputStream.readBytes()
                Base64.getEncoder().encodeToString(bytes)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun onSendMessagesWhenConnectionGoBack() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                val resoponse = getMessagesNoSendedFromRoom()
                resoponse.data!!.forEach{
                    onSaveMessage(it.message, "Group- " +  it.room, it.idRoom, it.type)
                }
            }
        }, 2000)
    }

    private suspend fun getMessagesNoSendedFromRoom(): Resource<List<SocketMessageReq>>  {
        return withContext(Dispatchers.IO) {
            roomMessageRepository.getMessagesNoSended()
        }
    }

    fun onSaveMessage(message: String, socketRoom: String, idServer: Int, type: RoomDataType) {
        try {
            var newMessage: String? = null
            if (type == RoomDataType.FILE) {
                newMessage = getBase64FromFile(Uri.parse(message), context) ?: ""
            } else {
                newMessage = message
            }

            val socketMessage = SocketMessageReq(socketRoom, newMessage, idServer, type)
            val jsonObject = JSONObject(Gson().toJson(socketMessage))

            mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
        } catch (e: OutOfMemoryError) {
        } catch (e: Exception) {
        }
    }

    fun addUsersToChats(userId: Int, chatId: Int, admin: Boolean) {
        val socketMessage = AddPeopleResponse(userId, chatId, admin)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_ADD_USER_CHAT_SEND.value, jsonObject)
    }

    fun deleteUsersToChats(userId: Int, chatId: Int, admin: Boolean) {
        val socketMessage = AddPeopleResponse(userId, chatId, admin)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_DELETE_USER_CHAT_SEND.value, jsonObject)
    }

    fun createChat(userId: Int, chatName: String, isPublic: Boolean, roomChatId: Int) {
        val socketMessage = CrateChat(userId, chatName, isPublic, roomChatId)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_CREATE_CHAT_SEND.value, jsonObject)
    }

    fun deleteChat(chatId: Int) {
        val socketMessage = DeleteChat(chatId)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_DELETE_CHAT_SEND.value, jsonObject)
    }
}
