package com.example.reto2_app_android.ui.publicChats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.model.ChatResponese_NewChat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class HomeViewModelFactory(
    private val remoteChatRepository: CommonChatRepository,
    private val roomChatRepository: CommonChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return HomeViewModel(remoteChatRepository, roomChatRepository) as T
    }

}

class HomeViewModel (
    private val chatRepository: CommonChatRepository,
    private val roomChatRepository: CommonChatRepository
) : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _items = MutableLiveData<Resource<List<ChatResponse_Chat>>>()

    val items: LiveData<Resource<List<ChatResponse_Chat>>> get() = _items

    private val _created = MutableLiveData<Resource<Int>>()
    val created: LiveData<Resource<Int>> get() = _created
    init {
        getChatFromRoom()
        updateChatsList()

    }
        fun getChatFromRoom(){
        viewModelScope.launch {
            val roomResponse = getChatsFromRoom()
            _items.value = roomResponse
        }
    }
    fun onAddChat(isPublic: Boolean, name: String) {
        val newChat = ChatResponse_Chat(0, name, null, null, null, null, isPublic)

        viewModelScope.launch {
            _created.value = createNewChat(newChat)
        }
    }


    private suspend fun createNewChat(newChat: ChatResponse_Chat): Resource<Int> {
        return withContext(Dispatchers.IO) {
            val id= 0
            chatRepository.createChat(newChat, id)
        }
    }

    fun updateChatsList(){
        viewModelScope.launch {
            val repoResponse = getChatsFromRepository()
            //_items.value = repoResponse
            // tODO meter en room los que no estan y meter en items.value los mismos
            //
          safeChatsInRoom(repoResponse.data!!, MyApp.db)
        }
    }

    suspend fun getChatsFromRoom(): Resource<List<ChatResponse_Chat>>{
        return  withContext(Dispatchers.IO){
            roomChatRepository.getChats()
        }
    }

    suspend fun getChatsFromRepository(): Resource<List<ChatResponse_Chat>>{
        return withContext((Dispatchers.IO)){
            chatRepository.getChats();
        }
    }

    suspend fun safeChatsInRoom(data: List<ChatResponse_Chat?>, db: AppDatabase): Boolean {
        try {
            if (data != null) {
                val chatDao = db.chatDao()
                val messagesDao = db.messageDao()
                val userDao = db.userDao()
                val userChatDao = db.userChatDao()
                val roleDao = db.roleDao()
                val userRoleDao = db.userRoleDao()
                data.forEach {
                    var roomId:Int? =1 ;
                    //Log.i("chats", it!!.id.toString())
                    val roomChat = it?.id?.let { it1 ->
                        RoomChat(
                            idServer = it1,
                            name = it!!.name,
                            isPublic = it!!.public,
                            createdAt = it!!.createdAt,
                            updatedAt = it!!.updatedAt
                        )
                    }
                    if (roomChat != null) {
                        roomId = chatDao.selectChatByServerId(roomChat.idServer)
                        if(roomId == null){
                            roomId = chatDao.insertChat(roomChat).toInt()
                        }
                    }

                    if (it != null) {
                        it.listUsers?.forEach {
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
                                userChatDao.insertUserChat(userChat)
                            }

                        }
                    }
                    //Añadir a la base de datos los mensajes
                    if (it != null) {
                        it.listMessages?.forEach{
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
                                val idMessage:Int? = messagesDao.selectById(message.idServer)
                                if(idMessage == null){
                                    messagesDao.insertMessage(message)
                                }
                            }
                        }
                    }
                }
            }
            getChatFromRoom()
            return true
        }catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


}