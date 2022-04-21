package com.example.khughes.machewidget.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.khughes.machewidget.UserInfo;

@Dao
public interface UserInfoDao {
    @Insert
    void insertUserInfo(UserInfo info);

    @Query("SELECT * FROM user_info WHERE userId LIKE :userId")
    UserInfo findUserInfo(String userId);

    @Delete
    void deleteUserInfo(UserInfo user);

    @Query("DELETE FROM user_info WHERE userId LIKE :userId")
    void deleteUserInfoByUserId(String userId);

    @Query("UPDATE user_info SET programState = :state WHERE userId = :userId")
    void updateProgramState(String state, String userId);

    @Update
    void updateUserInfo(UserInfo info);
}
