package com.example.reto2_app_android.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.repository.CommonUserRepository
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthLoginViewModel (
    private val userRepository: CommonUserRepository
) : ViewModel(){
    private val _login = MutableLiveData<Resource<AuthenticationResponse>>();
    val login : LiveData<Resource<AuthenticationResponse>> get() = _login

    fun loginUser(login: String, password: String) {
        val user = UserLogin(login, password)
        viewModelScope.launch {
            _login.value = logUserInRepository(user)
        }
    }

    private suspend fun logUserInRepository(user: UserLogin) : Resource<AuthenticationResponse> {
        Log.i("Aimar", user.toString())
        return withContext(Dispatchers.IO) {
            userRepository.loginUser(user)
        }
    }
}

class AuthLoginViewModelFactory(
    private val userRepository: CommonUserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthLoginViewModel(userRepository) as T
    }
}