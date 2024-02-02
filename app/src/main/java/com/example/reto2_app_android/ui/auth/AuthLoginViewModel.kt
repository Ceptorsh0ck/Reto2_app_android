package com.example.reto2_app_android.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.CommonUserRepository
import com.example.reto2_app_android.utils.JWTUtils
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthLoginViewModel (
    private val userRepositoryRemote: CommonUserRepository
) : ViewModel(){
    private val _login = MutableLiveData<Resource<AuthenticationResponse>>();
    val login : LiveData<Resource<AuthenticationResponse>> get() = _login

    private val _register = MutableLiveData<Resource<UserNew>>();
    val register : LiveData<Resource<UserNew>> get() = _register

    fun registerUser(user: UserNew) {
        viewModelScope.launch {
            val authResponse = registerUserRepository(user)

            _register.value = authResponse
        }
    }
    private suspend fun registerUserRepository(user: UserNew) : Resource<UserNew> {
        return withContext(Dispatchers.IO) {
            userRepositoryRemote.registerUser(user)
        }
    }

    fun loginUser(login: String, password: String, rememberMe: Boolean) {
        val user = UserLogin(login, password)
        viewModelScope.launch {
            val authResponse = logUserInRepository(user)
            authResponse.data?.let { MyApp.userPreferences.saveAuthToken(it.accessToken) }
            val loggedUser: User? = authResponse.data?.let { JWTUtils.decoded(it.accessToken) }
            if (loggedUser != null) {
                MyApp.userPreferences.saveLoggedUser(loggedUser)
                // Guardar el usuario según la preferencia Remember Me
                if (rememberMe) {
                    MyApp.userPreferences.saveRememberMeStatus(true)
                } else {
                    // Si no se recuerda, limpiar el usuario almacenado y el estado Remember Me
                    MyApp.userPreferences.saveRememberMeStatus(false)
                }
            }


            _login.value = logUserInRepository(user)


        }

    }

    private suspend fun logUserInRepository(user: UserLogin) : Resource<AuthenticationResponse> {
        return withContext(Dispatchers.IO) {
            userRepositoryRemote.loginUser(user)
        }
    }
}

class AuthLoginViewModelFactory(
    private val userRepositoryRemote: CommonUserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthLoginViewModel(userRepositoryRemote) as T
    }
}