/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

/**
 * A home automation device is represented by this class.
 *
 * @author          boto
 * Creation Date    6th June 2018
 */
public class DeviceInfo {

    private String id;
    private String ain;
    private String name;
    private String firmware;
    private String productName;
    private boolean present;
    private boolean on;
    private int voltage;
    private int power;
    private int energy;
    private int temperature;
    private int temperatureOffset;

    public DeviceInfo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAIN() {
        return ain;
    }

    public void setAIN(String ain) {
        this.ain = ain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
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
        return "id: " + id + ", " +
                "ain: " + ain + ", " +
                "firmware: " + firmware + ", " +
                "product name: " + productName + ", " +
                "name: " + name + ", " +
                "present: " + (present ? "on" : "off") + ", " +
                "state: " + (on ? "on" : "off") + ", " +
                "power: " + power + ", " +
                "energy: " + energy + ", " +
                "temperature: " + temperature;
    }
}
