/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

/**
 * Switch device info
 *
 * @author          boto
 * Creation Date    6th June 2018
 */
public class SwitchDeviceInfo extends DeviceInfo {

    public final static String DEVICE_TYPE = "SWITCH";

    private boolean on;
    private int voltage;
    private int power;
    private int energy;
    private int temperature;
    private int temperatureOffset;

    public SwitchDeviceInfo() {
        setDeviceType(DEVICE_TYPE);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTemperatureOffset() {
        return temperatureOffset;
    }

    public void setTemperatureOffset(int temperatureOffset) {
        this.temperatureOffset = temperatureOffset;
    }

    @Override
    public String toString() {
        return "switch device id: " + getId() + ", " +
                "ain: " + getAIN() + ", " +
                "firmware: " + getFirmware() + ", " +
                "product name: " + getProductName() + ", " +
                "name: " + getName() + ", " +
                "present: " + (isPresent() ? "yes" : "no") + ", " +
                "state: " + (on ? "on" : "off") + ", " +
                "power: " + power + ", " +
                "energy: " + energy + ", " +
                "temperature: " + temperature;
    }
}
