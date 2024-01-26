package com.example.reto2_app_android.ui.publicChats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.model.ChatResponse_User
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import com.example.reto2_app_android.data.repository.local.tables.RoomChat
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.local.tables.RoomRole
import com.example.reto2_app_android.data.repository.local.tables.RoomUser
import com.example.reto2_app_android.data.repository.local.tables.RoomUserChat
import com.example.reto2_app_android.data.repository.local.tables.RoomUserRol
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import javax.sql.rowset.serial.SerialBlob

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

    private val _items = MutableLiveData<Resource<ChatResponse_User>>()

    val items: LiveData<Resource<ChatResponse_User>> get() = _items

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

    fun updateChatsList(){
        viewModelScope.launch {
            val repoResponse = getChatsFromRepository()
            //_items.value = repoResponse
            // tODO meter en room los que no estan y meter en items.value los mismos
            //
          safeChatsInRoom(repoResponse.data, MyApp.db)
        }
    }

    suspend fun getChatsFromRoom(): Resource<ChatResponse_User>{
        return  withContext(Dispatchers.IO){
            roomChatRepository.getChats()
        }
    }

    suspend fun getChatsFromRepository(): Resource<ChatResponse_User>{
        return withContext((Dispatchers.IO)){
            chatRepository.getChats();
        }
    }

    suspend fun safeChatsInRoom(data: ChatResponse_User?, db: AppDatabase): Boolean {
        try {
            Log.i("Chats", data.toString())
            if (data != null) {
                val chatDao = db.chatDao()
                val messagesDao = db.messageDao()
                val userDao = db.userDao()
                val userChatDao = db.userChatDao()
                val roleDao = db.roleDao()
                val userRoleDao = db.userRoleDao()

                val user = data.id?.let {
                    RoomUser(
                        idServer = it,
                        email = data.email,
                        name = data.name,
                        surname1 = data.surname1,
                        surname2 = data.surname2,
                        address = data.address,
                        phoneNumber1 = data.phoneNumber1,
                        phoneNumber2 = data.phoneNumber2,
                        firstLogin = data.firstLogin,
                    )
                }

                if (user != null) {
                    userDao.insertUser(user)
                }

                data.roles?.forEach{
                    val role = RoomRole(
                        idServer = it.id,
                        name = it.name,
                        createdAt = null,
                        updatedAt = null
                    )

                    roleDao.insertRole(role)
                    val userRole = user?.let { it1 ->
                        role.idServer?.let { it2 ->
                            it1.idServer?.let { it3 ->
                                RoomUserRol(
                                    roleId = it2,
                                    userId = it3,
                                    createdAt = null,
                                    updatedAt = null
                                )
                            }
                        }
                    }

                    if (userRole != null) {
                        userRoleDao.insertUserRole(userRole)
                    }
                }

                data.listChats?.forEach {

                    val roomChat = RoomChat(
                        idServer = it.id,
                        name = it.name,
                        isPublic = it.public,
                        createdAt = null,
                        updatedAt = null
                    )

                    chatDao.insertChat(roomChat)

                    it.listUsers?.forEach {
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
                            userDao.insertUser(user)
                        }

                        val userChat = it.user.id?.let { it1 ->
                            roomChat.idServer?.let { it2 ->
                                RoomUserChat(
                                    userId = it1,
                                    chatId = it2,
                                    isAdmin = it.admin,
                                    createdAt = null,
                                    updatedAt = null
                                )
                            }
                        }
                        Log.i("Useeeee", userChat.toString())

                        if (userChat != null) {
                            userChatDao.insertUserChat(userChat)
                        }

                    }
                    //Añadir a la base de datos los mensajes
                    it.listMessages?.forEach{

                        val message = roomChat.idServer?.let { it1 ->
                            RoomMessages(
                                idServer = it.id,
                                content = it.content,
                                dataType = it.dataType,
                                createdAt = it.createdAt,
                                updatedAt = null,
                                chatId = it1,
                                userId = it.userId?.id ?: 0,
                            )
                        }
                        Log.i("Message", it.createdAt.toString())
                        if (message != null) {
                            messagesDao.insertMessage(message)
                        }

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

                        Log.d("Mesaje", user.toString())
                        if (user != null) {
                            userDao.insertUser(user)
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