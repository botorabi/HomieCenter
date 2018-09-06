/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;


import net.vrfun.homiecenter.ApplicationProperties;
import net.vrfun.homiecenter.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.net.*;
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

    private FritzBoxAuthentication fritzBoxAuthentication;
    private ResponseHandlerDeviceList handlerDeviceList ;
    private Requests requests;

    @Autowired
    private ApplicationProperties applicationProperties;

    private Pair<Instant, AuthStatus> cachedAuthStatus;
    private final static long AUTH_STATUS_MAX_CACHE_PERIOD_SEC = 60;
    private String fritzBoxURL;


    @Bean
    public FRITZBox fritzBox() {
        FRITZBox fritzBox = new FRITZBox();
        fritzBox.fritzBoxAuthentication = new FritzBoxAuthentication(getFritzBoxURL());
        fritzBox.handlerDeviceList = new ResponseHandlerDeviceList();
        fritzBox.requests = new Requests();

        LOGGER.info("Using FRITZ!Box URL: {}", getFritzBoxURL());

        return fritzBox;
    }

    @NotNull
    public FRITZBox withFritzBoxAuthentication(@NotNull FritzBoxAuthentication fritzBoxAuthentication) {
        this.fritzBoxAuthentication = fritzBoxAuthentication;
        return this;
    }

    @NotNull
    public FRITZBox withRequests(@NotNull Requests requests) {
        this.requests = requests;
        return this;
    }

    @NotNull
    public FRITZBox withResponseHandlerSwitchDeviceList(@NotNull ResponseHandlerDeviceList responseHandlerDeviceList) {
        this.handlerDeviceList = responseHandlerDeviceList;
        return this;
    }

    /**
     * In order to avoid requesting the FRITZ!Box for authentication state too often, use this method
     * which caches the state for a max time of 'AUTH_STATUS_MAX_CACHE_PERIOD_SEC'.
     */
    @NotNull
    public AuthStatus getCachedAuthStatus() throws Exception {
        if ((cachedAuthStatus == null) ||
                (Instant.now().isAfter(cachedAuthStatus.getFirst().plusSeconds(AUTH_STATUS_MAX_CACHE_PERIOD_SEC)))) {

            updateCachedAuthStatus();
        }

        return cachedAuthStatus.getSecond();
    }

    @NotNull
    public AuthStatus getAuthStatus() throws Exception {
        return fritzBoxAuthentication.getAuthStatus();
    }

    @NotNull
    public AuthStatus login(@NotNull final String userName, @NotNull final String password) throws Exception {
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

    @NotNull
    public List<DeviceInfo> getDevices() throws Exception {
        AuthStatus authStatus = loginIfNeeded();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("switchcmd", "getdevicelistinfos");

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        List<DeviceInfo> devices = new ArrayList<>();
        handlerDeviceList.read(response.getBody(), devices);

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

    public void handleDeviceCommand(long deviceId, @NotNull final String command) throws Exception {
        switch(command) {
            case "on":
                switchDevice(deviceId, true);
                break;
            case "off":
                switchDevice(deviceId, false);
                break;
            default:
                throw new Exception("Unsupported command: " + command);
        }
    }

    protected void switchDevice(Long deviceId, boolean on) throws Exception {
        AuthStatus authStatus = loginIfNeeded();
        DeviceInfo device = getDevice(deviceId);
        if (!(device instanceof SwitchDeviceInfo)) {
            throw new Exception("Could not switch the device (" + device.getAIN() + "), this is not a switch-device!");
        }

        SwitchDeviceInfo switchDevice = (SwitchDeviceInfo)getDevice(deviceId);

        if (switchDevice == null) {
            throw new Exception("Invalid ID: " + deviceId);
        }

        if (!switchDevice.isPresent()) {
            LOGGER.debug("ignoring switch command for device ({}), it is not present!", switchDevice.getAIN());
            return;
        }

        if (switchDevice.isOn() == on) {
            LOGGER.debug("ignoring switch command for device ({}), it is already {}!", switchDevice.getAIN(), (on ? "on": "off"));
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ain", switchDevice.getAIN());
        parameters.put("switchcmd", "setswitch" + (on ? "on" : "off"));

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Could not switch the device (" + switchDevice.getAIN() + ")!");
        }

        LOGGER.debug("device ({}) successfully switched {}", switchDevice.getAIN(), (on ? "on": "off"));
    }

    @NotNull
    protected AuthStatus loginIfNeeded() throws Exception {
        AuthStatus authStatus = getCachedAuthStatus();
        if (!authStatus.isAuthenticated()) {
            return login(applicationProperties.getFritzBoxUserName(), applicationProperties.getFritzBoxPassword());
        }
        return authStatus;
    }

    @NotNull
    protected ResponseEntity<String> requestHttpGET(@NotNull final String SID,
                                                  @NotNull final String relativeUrl,
                                                  @Nullable final Map<String, String> parameters) throws Exception {

        String url = getFritzBoxURL() + "/" + relativeUrl;
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("sid", SID);
        if (parameters != null) {
            urlParameters.putAll(parameters);
        }
        return requests.get(url, urlParameters);
    }

    @NotNull
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
