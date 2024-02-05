package com.example.reto2_app_android.data.repository.local

import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.AuthenticationResponse
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.CommonUserRepository
import com.example.reto2_app_android.data.repository.local.dao.UserDao
import com.example.reto2_app_android.ui.auth.PasswordRecoverRequest
import com.example.reto2_app_android.utils.Resource
import okhttp3.ResponseBody

class RoomUserDataSource: CommonUserRepository {
    private val userDao: UserDao = MyApp.db.userDao()
    override suspend fun loginUser(user: UserLogin): Resource<AuthenticationResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun registerUser(user: UserNew): Resource<UserNew> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserIdWithIdServer(userid: Int): Resource<Int> {
        return Resource.success(userDao.selectUserByServerId(userid))
    }

    override suspend fun recoverPassword(email: PasswordRecoverRequest): Resource<ResponseBody> {
        TODO("Not yet implemented")
    }
}