/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

/**
 * Heat Controller info
 *
 * @author          boto
 * Creation Date    6th September 2018
 */
public class HeatControllerDeviceInfo extends DeviceInfo {

    public final static String DEVICE_TPYE = "HEATCONTROLLER";

    private boolean batteryLow;
    private int batteryLevel;
    private int currentTemperature;
    private int setTemperature;
    private int comfortTemperature;
    private int economyTemperature;
    private boolean windowOpen;
    private int errorCode;


    public HeatControllerDeviceInfo() {
        setDeviceType(DEVICE_TPYE);
    }

    public boolean isBatteryLow() {
        return batteryLow;
    }

    public void setBatteryLow(boolean batteryLow) {
        this.batteryLow = batteryLow;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(int currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public int getSetTemperature() {
        return setTemperature;
    }

    public void setSetTemperature(int setTemperature) {
        this.setTemperature = setTemperature;
    }

    public int getComfortTemperature() {
        return comfortTemperature;
    }

    public void setComfortTemperature(int comfortTemperature) {
        this.comfortTemperature = comfortTemperature;
    }

    public int getEconomyTemperature() {
        return economyTemperature;
    }

    public void setEconomyTemperature(int economyTemperature) {
        this.economyTemperature = economyTemperature;
    }

    public boolean isWindowOpen() {
        return windowOpen;
    }

    public void setWindowOpen(boolean windowOpen) {
        this.windowOpen = windowOpen;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "heat controller id: " + getId() + ", " +
                "ain: " + getAIN() + ", " +
                "firmware: " + getFirmware() + ", " +
                "product name: " + getProductName() + ", " +
                "name: " + getName() + ", " +
                "present: " + (isPresent() ? "yes" : "no");
    }
}
