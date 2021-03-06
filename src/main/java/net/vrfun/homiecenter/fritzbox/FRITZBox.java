/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;


import net.vrfun.homiecenter.ApplicationProperties;
import net.vrfun.homiecenter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

/**
 * A service providing common FRITZ!Box related interaction mechanisms.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@Service
@Configuration
public class FRITZBox {

    private final Logger LOGGER = LoggerFactory.getLogger(FRITZBox.class);

    private final ApplicationProperties applicationProperties;

    private FritzBoxAuthentication fritzBoxAuthentication;
    private ResponseHandlerDeviceList responseHandlerDeviceList;
    private ResponseHandlerDeviceStats responseHandlerDeviceStats;
    private Requests requests;

    private Pair<Instant, AuthStatus> cachedAuthStatus;
    private final static long AUTH_STATUS_MAX_CACHE_PERIOD_SEC = 60;
    private String fritzBoxURL;

    @Autowired
    public FRITZBox(@NonNull final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public FRITZBox fritzBox(@NonNull final ApplicationProperties applicationProperties) {
        FRITZBox fritzBox = new FRITZBox(applicationProperties);
        fritzBox.fritzBoxAuthentication = new FritzBoxAuthentication(getFritzBoxURL());
        fritzBox.responseHandlerDeviceList = new ResponseHandlerDeviceList();
        fritzBox.responseHandlerDeviceStats = new ResponseHandlerDeviceStats();
        fritzBox.requests = new Requests();

        LOGGER.info("Using FRITZ!Box URL: {}", getFritzBoxURL());

        return fritzBox;
    }

    @NonNull
    public FRITZBox withFritzBoxAuthentication(@NonNull FritzBoxAuthentication fritzBoxAuthentication) {
        this.fritzBoxAuthentication = fritzBoxAuthentication;
        return this;
    }

    @NonNull
    public FRITZBox withRequests(@NonNull Requests requests) {
        this.requests = requests;
        return this;
    }

    @NonNull
    public FRITZBox withResponseHandlerDeviceList(@NonNull ResponseHandlerDeviceList responseHandlerDeviceList) {
        this.responseHandlerDeviceList = responseHandlerDeviceList;
        return this;
    }

    @NonNull
    public FRITZBox withResponseHandlerDeviceStats(@NonNull ResponseHandlerDeviceStats responseHandlerDeviceStats) {
        this.responseHandlerDeviceStats = responseHandlerDeviceStats;
        return this;
    }

    /**
     * In order to avoid requesting the FRITZ!Box for authentication state too often, use this method
     * which caches the state for a max time of 'AUTH_STATUS_MAX_CACHE_PERIOD_SEC'.
     */
    @NonNull
    public AuthStatus getCachedAuthStatus() throws Exception {
        if ((cachedAuthStatus == null) ||
                (Instant.now().isAfter(cachedAuthStatus.getFirst().plusSeconds(AUTH_STATUS_MAX_CACHE_PERIOD_SEC)))) {

            updateCachedAuthStatus();
        }

        return cachedAuthStatus.getSecond();
    }

    @NonNull
    public AuthStatus getAuthStatus() throws Exception {
        return fritzBoxAuthentication.getAuthStatus();
    }

    @NonNull
    public AuthStatus login(@NonNull final String userName, @NonNull final String password) throws Exception {
        discardCachedAuthStatus();

        AuthStatus authStatus = fritzBoxAuthentication.login(userName, password);
        if (!authStatus.isAuthenticated()) {
            LOGGER.debug("Failed to authenticate at FRITZ!Box");
            throw new Exception("Failed to authenticate at FRITZ!Box");
        }
        return authStatus;
    }

    public void logout() throws Exception {
        discardCachedAuthStatus();

        fritzBoxAuthentication.logout();
    }

    protected void updateCachedAuthStatus() throws Exception {
        cachedAuthStatus = Pair.of(Instant.now(), fritzBoxAuthentication.getAuthStatus());
    }

    protected void discardCachedAuthStatus() {
        cachedAuthStatus = null;
    }

    @NonNull
    public List<DeviceInfo> getDevices() throws Exception {
        AuthStatus authStatus = loginIfNeeded();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("switchcmd", "getdevicelistinfos");

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        List<DeviceInfo> devices = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            responseHandlerDeviceList.read(response.getBody(), devices);
        }

        return devices;
    }

    @Nullable
    public DeviceInfo getDevice(long deviceId) throws Exception {
        List<DeviceInfo> devices = getDevices();
        for(DeviceInfo device: devices) {
            if (device.getId().equals("" + deviceId)) {
                return device;
            }
        }
        return null;
    }

    @Nullable
    public DeviceStats getDeviceStats(@NonNull final String deviceAIN) throws Exception {
        AuthStatus authStatus = loginIfNeeded();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("switchcmd", "getbasicdevicestats");
        parameters.put("ain", deviceAIN);

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        DeviceStats deviceStats = new DeviceStats();
        deviceStats.setAIN(deviceAIN);

        if (response.getStatusCode() == HttpStatus.OK) {
            responseHandlerDeviceStats.read(response.getBody(), deviceStats);
        }

        return deviceStats;
    }

    public void handleDeviceCommand(long deviceId, @NonNull final String command) throws Exception {
        final String CMD_TEMPERATURE = "temperature=";
        final String CMD_ON = "on";
        final String CMD_OFF = "off";

        if (command.startsWith(CMD_TEMPERATURE)) {
            final String temperature = command.substring(CMD_TEMPERATURE.length());
            if (temperature.isEmpty()) {
                throw new Exception("Unsupported command: " + command);
            }
            try {
                final int desiredTemperature = Integer.parseInt(temperature);
                heatControlDeviceSetTemperature(deviceId, desiredTemperature);
            }
            catch(NumberFormatException exception) {
                throw new Exception("Invalid temperature in command: " + command);
            }
        }
        else {
            switch (command) {
                case CMD_ON:
                    switchDevice(deviceId, true);
                    break;
                case CMD_OFF:
                    switchDevice(deviceId, false);
                    break;
                default:
                    throw new Exception("Unsupported command: " + command);
            }
        }
    }

    protected void heatControlDeviceSetTemperature(long deviceId, int desiredTemperature) throws Exception {
        AuthStatus authStatus = loginIfNeeded();
        DeviceInfo device = getDevice(deviceId);
        if (!(device instanceof HeatControllerDeviceInfo)) {
            throw new Exception("Could not set temperature (" + device.getAIN() + "), this is not a heat controller device!");
        }

        HeatControllerDeviceInfo heatControllerDeviceInfo = (HeatControllerDeviceInfo)device;

        if (!heatControllerDeviceInfo.isPresent()) {
            LOGGER.debug("ignoring set temperature command for device ({}), it is not present!", heatControllerDeviceInfo.getAIN());
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ain", heatControllerDeviceInfo.getAIN());
        parameters.put("switchcmd", "sethkrtsoll");
        parameters.put("param", "" + desiredTemperature);

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Could not set temperature for the device (" + heatControllerDeviceInfo.getAIN() + ")!");
        }

        LOGGER.debug("device ({}) setting temperature to {}", heatControllerDeviceInfo.getAIN(), desiredTemperature);
    }

    protected void switchDevice(long deviceId, boolean on) throws Exception {
        AuthStatus authStatus = loginIfNeeded();
        DeviceInfo device = getDevice(deviceId);
        if (!(device instanceof SwitchDeviceInfo)) {
            throw new Exception("Could not switch the device (" + device.getAIN() + "), this is not a switch-device!");
        }

        SwitchDeviceInfo switchDeviceInfo = (SwitchDeviceInfo)device;

        if (!switchDeviceInfo.isPresent()) {
            LOGGER.debug("ignoring switch command for device ({}), it is not present!", switchDeviceInfo.getAIN());
            return;
        }

        if (switchDeviceInfo.isOn() == on) {
            LOGGER.debug("ignoring switch command for device ({}), it is already {}!", switchDeviceInfo.getAIN(), (on ? "on": "off"));
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ain", switchDeviceInfo.getAIN());
        parameters.put("switchcmd", "setswitch" + (on ? "on" : "off"));

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Could not switch the device (" + switchDeviceInfo.getAIN() + ")!");
        }

        LOGGER.debug("device ({}) successfully switched {}", switchDeviceInfo.getAIN(), (on ? "on": "off"));
    }

    @NonNull
    protected AuthStatus loginIfNeeded() throws Exception {
        AuthStatus authStatus = getCachedAuthStatus();
        if (!authStatus.isAuthenticated()) {
            return login(applicationProperties.getFritzBoxUserName(), applicationProperties.getFritzBoxPassword());
        }
        return authStatus;
    }

    @NonNull
    protected ResponseEntity<String> requestHttpGET(@NonNull final String SID,
                                                  @NonNull final String relativeUrl,
                                                  @Nullable final Map<String, String> parameters) throws Exception {

        String url = getFritzBoxURL() + "/" + relativeUrl;
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("sid", SID);
        if (parameters != null) {
            urlParameters.putAll(parameters);
        }
        return requests.get(url, urlParameters);
    }

    @NonNull
    protected String getFritzBoxURL() {
        if (fritzBoxURL != null) {
            return fritzBoxURL;
        }

        String url = applicationProperties.getFritzBoxUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        try {
            URL validateUrl = new URL(url);
            url = validateUrl.toString();
        }
        catch(MalformedURLException ex) {
            LOGGER.error("Invalid FRITZ!Box URL in homiecenter.properties file detected!");
            url = null;
        }
        Assert.notNull(url, "Invalid FRITZ!Box URL");

        fritzBoxURL = url;
        return url;
    }
}
