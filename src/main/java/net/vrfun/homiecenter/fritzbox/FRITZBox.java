/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;


import net.vrfun.homiecenter.model.DeviceInfo;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
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

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final static String DEFAULT_FRITZBOX_URL = "http://fritz.box";
    private final static String ENV_NAME_FRITZBOX_URL = "homiecenter_fritzbox_url";

    private String fritzBoxUrl;
    private Authentication authentication;
    private ResponseHandlerDeviceList handlerDeviceList ;
    private Requests requests;

    @Autowired
    private Environment environment;

    public FRITZBox() {
    }

    @Bean
    public FRITZBox fritzBox() {
        FRITZBox fritzBox = new FRITZBox();
        fritzBox.authentication = new Authentication(getFritzBoxURL());
        fritzBox.handlerDeviceList = new ResponseHandlerDeviceList();
        fritzBox.requests = new Requests();

        LOGGER.info("Using FRITZ!Box URL: {}", getFritzBoxURL());

        return fritzBox;
    }

    @NotNull
    public AuthStatus getAuthStatus() throws Exception {
        return authentication.getAuthStatus();
    }

    @NotNull
    public AuthStatus login(@NotNull final String userName, @NotNull final String password) throws Exception {
        AuthStatus authStatus = authentication.login(userName, password);
        if (!authStatus.isAuthenticated()) {
            LOGGER.debug("Failed to authenticate at FRITZ!Box");
            throw new Exception("Failed to authenticate at FRITZ!Box");
        }
        return authStatus;
    }

    public void logout() throws Exception {
        authentication.logout();
    }

    @NotNull
    public List<DeviceInfo> getDevices() throws Exception {
        AuthStatus authStatus = getAuthStatus();

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

    public void handleDeviceCommand(Long deviceId, String command) throws Exception {
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

    private void switchDevice(Long deviceId, boolean on) throws Exception {
        AuthStatus authStatus = getAuthStatus();
        DeviceInfo device = getDevice(deviceId);

        if (device == null) {
            throw new Exception("Invalid ID: " + deviceId);
        }

        if (!device.isPresent()) {
            LOGGER.debug("ignoring switch command for device ({}), it is not present!", device.getAIN());
            return;
        }

        if (device.isOn() == on) {
            LOGGER.debug("ignoring switch command for device ({}), it is already {}!", device.getAIN(), (on ? "on": "off"));
            return;
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ain", device.getAIN());
        parameters.put("switchcmd", "setswitch" + (on ? "on" : "off"));

        ResponseEntity<String> response = requestHttpGET(
                authStatus.getSID(),
                "webservices/homeautoswitch.lua",
                parameters);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Could not switch the device (" + device.getAIN() + ")!");
        }

        LOGGER.debug("device ({}) successfully switched {}", device.getAIN(), (on ? "on": "off"));
    }

    @NotNull
    private ResponseEntity<String> requestHttpGET(@NotNull final String SID,
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

    @Nullable
    private String getFritzBoxURL() {
        if (fritzBoxUrl != null) {
            return fritzBoxUrl;
        }
        LOGGER.info("Checking for environment '{}'...", ENV_NAME_FRITZBOX_URL);
        String url = environment.getProperty(ENV_NAME_FRITZBOX_URL);
        if (url != null) {
            LOGGER.info("  Found '{}', using URL '{}'", ENV_NAME_FRITZBOX_URL, url);
            fritzBoxUrl = url;
        }
        else {
            LOGGER.info("  Did not find '{}', using default URL '{}'", ENV_NAME_FRITZBOX_URL, DEFAULT_FRITZBOX_URL);
            fritzBoxUrl = DEFAULT_FRITZBOX_URL;
        }

        return fritzBoxUrl;
    }
}
