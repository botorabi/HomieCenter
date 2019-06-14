/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.utils.HashGenerator;
import org.slf4j.*;
import org.springframework.http.*;

import javax.validation.constraints.NotNull;
import java.security.*;
import java.util.*;

/**
 * This class performs a user authentication on a FRITZ!Box.
 *
 * @author          boto
 * Creation Date    6th June 2018
 */
public class FritzBoxAuthentication {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private String lastSID = "";
    private String fritzBoxAddress;

    private ResponseHandlerAuthStatus responseHandlerAuthStatus;
    private Requests requests;

    public FritzBoxAuthentication() {
    }

    public FritzBoxAuthentication(@NotNull final String fritzBoxAddress) {
        this.fritzBoxAddress = fritzBoxAddress;
        responseHandlerAuthStatus = new ResponseHandlerAuthStatus();
        requests = new Requests();
    }

    @NotNull
    public FritzBoxAuthentication withFritzBoxAddress(@NotNull final String fritzBoxAddress) {
        this.fritzBoxAddress = fritzBoxAddress;
        return this;
    }

    @NotNull
    public FritzBoxAuthentication withResponseHandlerAuthStatus(@NotNull ResponseHandlerAuthStatus responseHandlerAuthStatus) {
        this.responseHandlerAuthStatus = responseHandlerAuthStatus;
        return this;
    }

    @NotNull
    public FritzBoxAuthentication withRequests(@NotNull Requests requests) {
        this.requests = requests;
        return this;
    }

    @NotNull
    public AuthStatus getAuthStatus() throws Exception {
        ResponseEntity<String> response = getConnectionState();
        if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
            lastSID = "";
        }
        else if (response.getStatusCode() != HttpStatus.OK) {
            lastSID = "";
            throw new Exception("Requesting FRITZ!Box failed with status: {}" + response.getStatusCode());
        }
        AuthStatus authStatus = new AuthStatus();
        responseHandlerAuthStatus.read(response.getBody(), authStatus);
        return authStatus;
    }

    @NotNull
    public AuthStatus login(@NotNull final String userName, @NotNull final String password) throws Exception {
        AuthStatus authStatus = getAuthStatus();
        if (authStatus.isAuthenticated()) {
            return authStatus;
        }

        return authenticate(authStatus.getChallenge(), userName, password);
    }

    public void logout() throws Exception {
        AuthStatus authStatus = getAuthStatus();
        if (!authStatus.isAuthenticated()) {
            return;
        }

        try {
            String url = fritzBoxAddress + "/login_sid.lua";

            Map<String, String> parameters = new HashMap<>();
            parameters.put("sid", authStatus.getSID());
            parameters.put("logout", "");

            requests.post(url, parameters);
        }
        catch (Throwable throwable) {
            throw new Exception("Could not logout user, reason: " + throwable.getMessage());
        }
    }

    @NotNull
    protected AuthStatus authenticate(@NotNull final String challenge,
                                      @NotNull final String userName,
                                      @NotNull final String password) throws Exception {

        String url = fritzBoxAddress + "/login_sid.lua";
        Map<String, String> params = new HashMap<>();
        params.put("username", userName);
        params.put("response", createResponse(challenge, password));

        try {
            ResponseEntity<String> response = requests.get(url, params);
            if (response.getStatusCode() == HttpStatus.OK) {
                AuthStatus status = new AuthStatus();
                responseHandlerAuthStatus.read(response.getBody(), status);
                if (!status.hasValidSID()) {
                    LOGGER.warn("Could not get a SID!");
                }
                lastSID = status.getSID();
                return status;
            }
            else {
                lastSID = "";
                throw new Exception("Requesting FRITZ!Box failed with status: {}" + response.getStatusCode());
            }
        }
        catch(Throwable throwable) {
            LOGGER.warn("Could not login into FRITZ!Box, reason: {}", throwable.getMessage());
            throw new Exception("Could not login into FRITZ!Box");
        }
    }

    @NotNull
    protected ResponseEntity<String> getConnectionState() throws Exception {
        ResponseEntity<String> response;
        String url = fritzBoxAddress + "/login_sid.lua";

        if (!lastSID.isEmpty()) {
            url += "?sid=" + lastSID;
        }

        try {
            response = requests.get(url, null);
        }
        catch(Throwable throwable) {
            LOGGER.warn("Could not connect FRITZ!Box, reason: {}", throwable.getMessage());
            throw new Exception("Could not connect FRITZ!Box with address: " + url);
        }

        return response;
    }

    @NotNull
    protected String createResponse(@NotNull final String challenge, @NotNull final String password) throws Exception {
        return challenge + "-" + HashGenerator.createMD5((challenge + "-" + preparePassword(password)).getBytes("UTF-16LE"));
    }

    @NotNull
    protected String preparePassword(@NotNull final String password) {
        //! NOTE all non-ascii chars have to be replaced by dots
        return password.replaceAll("[^\\x00-\\x7F]", ".");
    }
}
