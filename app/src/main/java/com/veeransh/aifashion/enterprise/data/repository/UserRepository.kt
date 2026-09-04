package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.UserDao
import com.veeransh.aifashion.enterprise.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAll()

    suspend fun getUserById(uid: String): UserEntity? = userDao.getById(uid)

    suspend fun saveUser(user: UserEntity) = userDao.insert(user)

    suspend fun updateUser(user: UserEntity) = userDao.update(user)

    suspend fun deleteUser(uid: String) = userDao.delete(uid)
}
