package com.example.reto2_app_android.data.repository.remote

import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.UserLogin
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.CommonUserRepository
import com.example.reto2_app_android.ui.auth.PasswordRecoverRequest
import com.example.reto2_app_android.utils.Resource

class RemoteUsersDataSource: BaseDataSource(), CommonUserRepository {

    override suspend fun loginUser(user: UserLogin) = getResult {
        RetrofitClient.apiInterface.loginUser(user)
    }

    override suspend fun registerUser(user: UserNew) = getResult {
        RetrofitClient.apiInterface.registerUser(user)
    }

    override suspend fun recoverPassword(email: PasswordRecoverRequest) = getResult {
        RetrofitClientLaravel.apiInterfaceLaravel.recoverPassword(email)
    }

//    override suspend fun registerUser(user: User) = getResult {
//        RetrofitClient.apiInterface.registerUser(user)
//    }
//
//    override suspend fun changePassword(user: User) = getResult {
//        RetrofitClient.apiInterface.changePassword(user)
//    }
//
//    override suspend fun checkEmail(registrationCheck: RegistrationCheck) = getResult {
//        RetrofitClient.apiInterface.checkEmail(registrationCheck)
//    }
//
//    override suspend fun checkLogin(registrationCheck: RegistrationCheck) = getResult {
//        RetrofitClient.apiInterface.checkLogin(registrationCheck)
//    }

}