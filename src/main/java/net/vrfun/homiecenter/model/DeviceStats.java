/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

import javax.validation.constraints.NotNull;

/**
 * Device statistics
 *
 * @author          boto
 * Creation Date    25th September 2018
 */
public class DeviceStats {

    private String ain = "";

    @NotNull
    private DeviceStatsValues temperature = new DeviceStatsValues();

    @NotNull
    private DeviceStatsValues power = new DeviceStatsValues();

    @NotNull
    private DeviceStatsValues energy = new DeviceStatsValues();

    public DeviceStats() {
    }

    @NotNull
    public String getAIN() {
        return ain;
    }

    public void setAIN(@NotNull final String ain) {
        this.ain = ain;
    }

    @NotNull
    public DeviceStatsValues getTemperature() {
        return temperature;
    }

    public void setTemperature(@NotNull final DeviceStatsValues temperature) {
        this.temperature = temperature;
    }

    @NotNull
    public DeviceStatsValues getPower() {
        return power;
    }

    public void setPower(@NotNull final DeviceStatsValues power) {
        this.power = power;
    }

    @NotNull
    public DeviceStatsValues getEnergy() {
        return energy;
    }

    public void setEnergy(@NotNull final DeviceStatsValues energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "device stats, " +
                "ain: " + getAIN();
    }
}
