/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

import org.springframework.lang.NonNull;

/**
 * Device statistics
 *
 * @author          boto
 * Creation Date    25th September 2018
 */
public class DeviceStats {

    private String ain = "";

    @NonNull
    private DeviceStatsValues temperature = new DeviceStatsValues();

    @NonNull
    private DeviceStatsValues power = new DeviceStatsValues();

    @NonNull
    private DeviceStatsValues energy = new DeviceStatsValues();

    public DeviceStats() {
    }

    @NonNull
    public String getAIN() {
        return ain;
    }

    public void setAIN(@NonNull final String ain) {
        this.ain = ain;
    }

    @NonNull
    public DeviceStatsValues getTemperature() {
        return temperature;
    }

    public void setTemperature(@NonNull final DeviceStatsValues temperature) {
        this.temperature = temperature;
    }

    @NonNull
    public DeviceStatsValues getPower() {
        return power;
    }

    public void setPower(@NonNull final DeviceStatsValues power) {
        this.power = power;
    }

    @NonNull
    public DeviceStatsValues getEnergy() {
        return energy;
    }

    public void setEnergy(@NonNull final DeviceStatsValues energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "device stats, " +
                "ain: " + getAIN();
    }
}
