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

    private final List<VehicleInfo> mVehicleList;
    private VehicleInfo mVehicleInfo;
    private final List<UserInfo> mUserList;
    private UserInfo mUserInfo;

    InfoRepository(Context context) {
        mContext = context;
        mVehicleInfoDao = VehicleInfoDatabase.getInstance(context).vehicleInfoDao();
        mUserInfoDao = UserInfoDatabase.getInstance(context).userInfoDao();

        mVehicleList = mVehicleInfoDao.findVehicleInfo();
        mUserList = mUserInfoDao.findUserInfo();

        String userId = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.userId_key), null);
        mUserInfo = new UserInfo();
        if (userId != null) {
            mUserInfo = mUserInfoDao.findUserInfo(userId);
        } else {
            LogFile.e(context, MainActivity.CHANNEL_ID, "InfoRepository(): default settings userId is null");
            List<UserInfo> users = mUserInfoDao.findUserInfo();
            if (users.size() > 0) {
                mUserInfo = users.get(0);
                LogFile.e(context, MainActivity.CHANNEL_ID, "InfoRepository(): fallback to userId " + mUserInfo.getUserId());
            }
        }
    }

    public VehicleInfo getVehicleByVIN(String VIN) {
        ArrayList<String> VINs = new ArrayList<>();
        for(VehicleInfo vehicleInfo: mVehicleList) {
            if(VIN.equals(vehicleInfo.getVIN())) {
                return vehicleInfo;
            }
        }
        return null;
    }

    public void setVehicle(VehicleInfo info) {
        mVehicleInfo = info;
        VehicleInfoDatabase.databaseWriteExecutor.execute(() -> {
            mVehicleInfoDao.updateVehicleInfo(info);
        });
    }

    public UserInfo getUser() {
        return mUserInfo;
    }

    public List<VehicleInfo> getVehicles() {
        return mVehicleList;
    }

    public ArrayList<String> getVehiclesVINsByUserId(String userId) {
        ArrayList<String> VINs = new ArrayList<>();
        for(VehicleInfo vehicleInfo: mVehicleList) {
            if(userId.equals(vehicleInfo.getUserId())) {
                VINs.add(vehicleInfo.getVIN());
            }
        }
        return VINs;
    }
}

