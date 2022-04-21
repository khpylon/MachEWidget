package com.example.khughes.machewidget.CarStatus;

import androidx.room.Embedded;
import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Vehiclestatus {

    @SerializedName("vin")
    @Expose
    @Ignore
    private String vin;
    @SerializedName("lockStatus")
    @Expose
    @Embedded
    private LockStatus lockStatus;
    @SerializedName("alarm")
    @Expose
    @Embedded
    private Alarm alarm;
    //        @SerializedName("PrmtAlarmEvent")
//        @Expose
//        private PrmtAlarmEvent prmtAlarmEvent;
    @SerializedName("odometer")
    @Expose
    @Embedded
    private Odometer odometer;
    @SerializedName("fuel")
    @Expose
    @Embedded
    private Fuel fuel;
    @SerializedName("gps")
    @Expose
    @Embedded
    private Gps gps;
    @SerializedName("remoteStart")
    @Expose
    @Embedded
    private RemoteStart remoteStart;
    @SerializedName("remoteStartStatus")
    @Expose
    @Embedded
    private RemoteStartStatus remoteStartStatus;
    @SerializedName("battery")
    @Expose
    @Embedded
    private Battery battery;
    //        @SerializedName("oil")
//        @Expose
//        private Oil oil;
//        @SerializedName("tirePressure")
//        @Expose
//        private TirePressure tirePressure;
//        @SerializedName("authorization")
//        @Expose
//        private String authorization;
    @SerializedName("TPMS")
    @Expose
    @Embedded
    private TPMS tpms;
    //        @SerializedName("firmwareUpgInProgress")
//        @Expose
//        private FirmwareUpgInProgress firmwareUpgInProgress;
    @SerializedName("deepSleepInProgress")
    @Expose
    @Embedded
    private DeepSleepInProgress deepSleepInProgress;
    //        @SerializedName("ccsSettings")
//        @Expose
//        private CcsSettings ccsSettings;
    @SerializedName("lastRefresh")
    @Expose
    private String lastRefresh;
    @SerializedName("lastModifiedDate")
    @Expose
    private String lastModifiedDate;
    //        @SerializedName("serverTime")
//        @Expose
//        private String serverTime;
    @SerializedName("batteryFillLevel")
    @Expose
    @Embedded
    private BatteryFillLevel batteryFillLevel;
    @SerializedName("elVehDTE")
    @Expose
    @Embedded
    private ElVehDTE elVehDTE;
    //        @SerializedName("hybridModeStatus")
//        @Expose
//        private HybridModeStatus hybridModeStatus;
    @SerializedName("chargingStatus")
    @Expose
    @Embedded
    private ChargingStatus chargingStatus;
    @SerializedName("plugStatus")
    @Expose
    @Embedded
    private PlugStatus plugStatus;
    //        @SerializedName("chargeStartTime")
//        @Expose
//        private ChargeStartTime chargeStartTime;
    @SerializedName("chargeEndTime")
    @Expose
    @Embedded
    private ChargeEndTime chargeEndTime;
    //        @SerializedName("preCondStatusDsply")
//        @Expose
//        private PreCondStatusDsply preCondStatusDsply;
//        @SerializedName("chargerPowertype")
//        @Expose
//        private Object chargerPowertype;
//        @SerializedName("batteryPerfStatus")
//        @Expose
//        private BatteryPerfStatus batteryPerfStatus;
//        @SerializedName("outandAbout")
//        @Expose
//        private OutandAbout outandAbout;
//        @SerializedName("batteryChargeStatus")
//        @Expose
//        private BatteryChargeStatus batteryChargeStatus;
//        @SerializedName("dcFastChargeData")
//        @Expose
//        private DcFastChargeData dcFastChargeData;
    @SerializedName("windowPosition")
    @Expose
    @Embedded
    private WindowPosition windowPosition;
    @SerializedName("doorStatus")
    @Expose
    @Embedded
    private DoorStatus doorStatus;
    @SerializedName("ignitionStatus")
    @Expose
    @Embedded
    private IgnitionStatus ignitionStatus;
//        @SerializedName("batteryTracLowChargeThreshold")
//        @Expose
//        private BatteryTracLowChargeThreshold batteryTracLowChargeThreshold;
//        @SerializedName("battTracLoSocDDsply")
//        @Expose
//        private BattTracLoSocDDsply battTracLoSocDDsply;
//        @SerializedName("dieselSystemStatus")
//        @Expose
//        private Object dieselSystemStatus;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public LockStatus getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(LockStatus lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

//        public PrmtAlarmEvent getPrmtAlarmEvent() {
//            return prmtAlarmEvent;
//        }
//
//        public void setPrmtAlarmEvent(PrmtAlarmEvent prmtAlarmEvent) {
//            this.prmtAlarmEvent = prmtAlarmEvent;
//        }

    public Odometer getOdometer() {
        return odometer;
    }

    public void setOdometer(Odometer odometer) {
        this.odometer = odometer;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public RemoteStart getRemoteStart() {
        return remoteStart;
    }

    public void setRemoteStart(RemoteStart remoteStart) {
        this.remoteStart = remoteStart;
    }

    public RemoteStartStatus getRemoteStartStatus() {
        return remoteStartStatus;
    }

    public void setRemoteStartStatus(RemoteStartStatus remoteStartStatus) {
        this.remoteStartStatus = remoteStartStatus;
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

//        public Oil getOil() {
//            return oil;
//        }
//
//        public void setOil(Oil oil) {
//            this.oil = oil;
//        }
//
//        public TirePressure getTirePressure() {
//            return tirePressure;
//        }
//
//        public void setTirePressure(TirePressure tirePressure) {
//            this.tirePressure = tirePressure;
//        }

//        public String getAuthorization() {
//            return authorization;
//        }
//
//        public void setAuthorization(String authorization) {
//            this.authorization = authorization;
//        }

    public TPMS getTpms() {
        return tpms;
    }

    public void setTpms(TPMS tpms) {
        this.tpms = tpms;
    }

//        public FirmwareUpgInProgress getFirmwareUpgInProgress() {
//            return firmwareUpgInProgress;
//        }
//
//        public void setFirmwareUpgInProgress(FirmwareUpgInProgress firmwareUpgInProgress) {
//            this.firmwareUpgInProgress = firmwareUpgInProgress;
//        }

    public DeepSleepInProgress getDeepSleepInProgress() {
        return deepSleepInProgress;
    }

    public void setDeepSleepInProgress(DeepSleepInProgress deepSleepInProgress) {
        this.deepSleepInProgress = deepSleepInProgress;
    }

//        public CcsSettings getCcsSettings() {
//            return ccsSettings;
//        }
//
//        public void setCcsSettings(CcsSettings ccsSettings) {
//            this.ccsSettings = ccsSettings;
//        }

    public String getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(String lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    //        public String getServerTime() {
//            return serverTime;
//        }
//
//        public void setServerTime(String serverTime) {
//            this.serverTime = serverTime;
//        }
//
    public BatteryFillLevel getBatteryFillLevel() {
        return batteryFillLevel;
    }

    public void setBatteryFillLevel(BatteryFillLevel batteryFillLevel) {
        this.batteryFillLevel = batteryFillLevel;
    }

    public ElVehDTE getElVehDTE() {
        return elVehDTE;
    }

    public void setElVehDTE(ElVehDTE elVehDTE) {
        this.elVehDTE = elVehDTE;
    }

    //        public HybridModeStatus getHybridModeStatus() {
//            return hybridModeStatus;
//        }
//
//        public void setHybridModeStatus(HybridModeStatus hybridModeStatus) {
//            this.hybridModeStatus = hybridModeStatus;
//        }
//
    public ChargingStatus getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(ChargingStatus chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public PlugStatus getPlugStatus() {
        return plugStatus;
    }

    public void setPlugStatus(PlugStatus plugStatus) {
        this.plugStatus = plugStatus;
    }
    //
//        public ChargeStartTime getChargeStartTime() {
//            return chargeStartTime;
//        }
//
//        public void setChargeStartTime(ChargeStartTime chargeStartTime) {
//            this.chargeStartTime = chargeStartTime;
//        }
//
    public ChargeEndTime getChargeEndTime() {
        return chargeEndTime;
    }

    public void setChargeEndTime(ChargeEndTime chargeEndTime) {
        this.chargeEndTime = chargeEndTime;
    }
//
//        public PreCondStatusDsply getPreCondStatusDsply() {
//            return preCondStatusDsply;
//        }
//
//        public void setPreCondStatusDsply(PreCondStatusDsply preCondStatusDsply) {
//            this.preCondStatusDsply = preCondStatusDsply;
//        }
//
//        public Object getChargerPowertype() {
//            return chargerPowertype;
//        }
//
//        public void setChargerPowertype(Object chargerPowertype) {
//            this.chargerPowertype = chargerPowertype;
//        }
//
//        public BatteryPerfStatus getBatteryPerfStatus() {
//            return batteryPerfStatus;
//        }
//
//        public void setBatteryPerfStatus(BatteryPerfStatus batteryPerfStatus) {
//            this.batteryPerfStatus = batteryPerfStatus;
//        }
//
//        public OutandAbout getOutandAbout() {
//            return outandAbout;
//        }
//
//        public void setOutandAbout(OutandAbout outandAbout) {
//            this.outandAbout = outandAbout;
//        }
//
//        public BatteryChargeStatus getBatteryChargeStatus() {
//            return batteryChargeStatus;
//        }
//
//        public void setBatteryChargeStatus(BatteryChargeStatus batteryChargeStatus) {
//            this.batteryChargeStatus = batteryChargeStatus;
//        }
//
//        public DcFastChargeData getDcFastChargeData() {
//            return dcFastChargeData;
//        }
//
//        public void setDcFastChargeData(DcFastChargeData dcFastChargeData) {
//            this.dcFastChargeData = dcFastChargeData;
//        }

    public WindowPosition getWindowPosition() {
        return windowPosition;
    }

    public void setWindowPosition(WindowPosition windowPosition) {
        this.windowPosition = windowPosition;
    }

    public DoorStatus getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(DoorStatus doorStatus) {
        this.doorStatus = doorStatus;
    }

    public IgnitionStatus getIgnitionStatus() {
        return ignitionStatus;
    }

    public void setIgnitionStatus(IgnitionStatus ignitionStatus) {
        this.ignitionStatus = ignitionStatus;
    }

//        public BatteryTracLowChargeThreshold getBatteryTracLowChargeThreshold() {
//            return batteryTracLowChargeThreshold;
//        }
//
//        public void setBatteryTracLowChargeThreshold(BatteryTracLowChargeThreshold batteryTracLowChargeThreshold) {
//            this.batteryTracLowChargeThreshold = batteryTracLowChargeThreshold;
//        }

//        public BattTracLoSocDDsply getBattTracLoSocDDsply() {
//            return battTracLoSocDDsply;
//        }
//
//        public void setBattTracLoSocDDsply(BattTracLoSocDDsply battTracLoSocDDsply) {
//            this.battTracLoSocDDsply = battTracLoSocDDsply;
//        }

//        public Object getDieselSystemStatus() {
//            return dieselSystemStatus;
//        }
//
//        public void setDieselSystemStatus(Object dieselSystemStatus) {
//            this.dieselSystemStatus = dieselSystemStatus;
//        }

}
