package com.example.khughes.machewidget.db

import androidx.room.*
import com.example.khughes.machewidget.UserInfo

@Dao
interface UserInfoDao {
    @Insert
    fun insertUserInfo(info: UserInfo)

    @Query("SELECT * FROM user_info")
    fun findUserInfo(): List<UserInfo>

    @Query("SELECT * FROM user_info WHERE userId LIKE :userId")
    fun findUserInfo(userId: String): UserInfo?

    @Delete
    fun deleteUserInfo(user: UserInfo)

    @Query("DELETE FROM user_info WHERE userId LIKE :userId")
    fun deleteUserInfoByUserId(userId: String)

    @Query("DELETE FROM user_info")
    fun deleteAllUserInfo()

    @Query("UPDATE user_info SET programState = :state WHERE userId = :userId")
    fun updateProgramState(state: String, userId: String)

    @Query("UPDATE user_info SET lastModified = :mod WHERE userId = :userId")
    fun updateLastModified(mod: String, userId: String)

    @Update
    fun updateUserInfo(info: UserInfo)
}