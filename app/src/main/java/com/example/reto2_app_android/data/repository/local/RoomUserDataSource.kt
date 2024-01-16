package com.example.reto2_app_android.data.repository.local

import androidx.room.Dao
import androidx.room.Query
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.data.DbUser
import com.example.reto2_app_android.data.User
import com.example.reto2_app_android.data.repository.CommonUserRepository
import com.example.reto2_app_android.utils.Resource
/*
class RoomUserDataSource : CommonUserRepository {

//    private val userDao: UserDao = MyApp.db.userDao()
//
//    override suspend fun getUsers(): Resource<List<User>> {
//        val response = userDao.getUsers().map{ it.toUser()}
//        return Resource.success(response)
//    }

}

fun User.toDbUser() = login?.let { DbUser(id, it, password) }
fun DbUser.toUser() = User(id, login, password)

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<DbUser>
}*/