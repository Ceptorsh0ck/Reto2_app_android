package com.example.reto2_app_android.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.repository.CommonChatRepository
import com.example.reto2_app_android.data.repository.remote.RemoteChatsDataSource
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModelFactory(
    private val chatRepository: RemoteChatsDataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return HomeViewModel(chatRepository) as T
    }

}

class HomeViewModel (
    private val chatRepository: CommonChatRepository
) : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _items = MutableLiveData<Resource<List<Chat>>>()

    val items: LiveData<Resource<List<Chat>>> get() = _items

    init {
        Log.i("Aimar", "Funciona")
        updateChatsList()
    }

    fun updateChatsList(){
        viewModelScope.launch {
            val repoResponse = getChatsFromRepository()
            _items.value = repoResponse
        }
    }

    suspend fun getChatsFromRepository(): Resource<List<Chat>>{
        Log.i("Aimar", "Fuasdanciona")
        return withContext((Dispatchers.IO)){
            chatRepository.getChats();
        }
    }

}