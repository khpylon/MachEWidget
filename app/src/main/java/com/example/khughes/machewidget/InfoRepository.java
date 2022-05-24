package com.example.khughes.machewidget;

import android.content.Context;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.db.UserInfoDao;
import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class InfoRepository {

    private final VehicleInfoDao mVehicleInfoDao;
    private final UserInfoDao mUserInfoDao;
    private final Context mContext;
    private final String mVIN_key;

    private List<VehicleInfo> mVehicleList;
    private VehicleInfo mVehicleInfo;
    private final List<UserInfo> mUserList;
    private UserInfo mUserInfo;

    InfoRepository(Context context) {
        mContext = context;
        mVehicleInfoDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao();
        mUserInfoDao = UserInfoDatabase.getInstance(context).userInfoDao();
        mVIN_key = context.getResources().getString(R.string.VIN_key);

        mVehicleList = mVehicleInfoDao.findVehicleInfo();
        mUserList = mUserInfoDao.findUserInfo();

        String VIN = PreferenceManager.getDefaultSharedPreferences(context).getString(mVIN_key, null);
        mVehicleInfo = new VehicleInfo();
        mUserInfo = new UserInfo();
        if (VIN != null) {
            mVehicleInfo = mVehicleInfoDao.findVehicleInfoByVIN(VIN);
            if (mVehicleInfo != null) {
                String userId = mVehicleInfo.getUserId();
                if (userId != null) {
                    mUserInfo = mUserInfoDao.findUserInfo(mVehicleInfo.getUserId());
                } else {
                    LogFile.e(context, MainActivity.CHANNEL_ID, "InfoRepository(): userId " + mVehicleInfo.getUserId() +
                            " not found in database for VIN " + VIN);
                    List<UserInfo> users = mUserInfoDao.findUserInfo();
                    if (users.size() > 0) {
                        mUserInfo = users.get(0);
                        LogFile.e(context, MainActivity.CHANNEL_ID, "InfoRepository(): fallback to userId " + mUserInfo.getUserId());
                    }
                }
            }
        }
    }

    public VehicleInfo getVehicle() {
        return mVehicleInfo;
    }

}

