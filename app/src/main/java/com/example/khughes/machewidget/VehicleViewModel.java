package com.example.khughes.machewidget;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.util.List;

public class VehicleViewModel extends AndroidViewModel {

    private final VehicleRepository mRepository;
    private final LiveData<List<VehicleIds>> mAllVehicles;

    public VehicleViewModel (Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mAllVehicles = mRepository.getAllVehicles();
    }

    LiveData<List<VehicleIds>> getAllVehicles() { return mAllVehicles; }

    public void setEnable(String VIN, boolean value) { mRepository.enable(VIN,value);}

    public int countEnabledVehicle() {
        int count = 0;
        for(VehicleIds id: mAllVehicles.getValue()) {
            if(id.isEnabled()) {
                ++count;
            }
        }
        return count;
    }

    class VehicleRepository {

        private final VehicleInfoDao mVehDao;
        private final LiveData<List<VehicleIds>> mAllVehicles;

        VehicleRepository(Application application) {
            mVehDao = VehicleInfoDatabase.getInstance(application).vehicleInfoDao();
            mAllVehicles = mVehDao.getLiveDataVehicleInfo();
        }

        LiveData<List<VehicleIds>> getAllVehicles() {
            return mAllVehicles;
        }

        void enable(String VIN, boolean value) {
            new Thread( () -> mVehDao.updateEnable(VIN, value)).start();
        }
    }
}
