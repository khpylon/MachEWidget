package com.example.khughes.machewidget;

public class Profile {
    private String VIN;         // the "key" for the object
    private String alias;
    public Profile(String VIN) {
        this.VIN = VIN;
        this.alias = "";
    }

    public Profile(String VIN, String alias) {
        this.VIN = VIN;
        this.alias = alias;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getProfileName() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
