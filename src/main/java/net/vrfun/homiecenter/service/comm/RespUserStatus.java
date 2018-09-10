/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service.comm;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Response for user data.
 *
 * @author          boto
 * Creation Date    14th June 2018
 */
public class RespUserStatus {

    private String appVersion;

    private String name;

    private boolean authenticated;

    private String role;

    public RespUserStatus(@NotNull final String appVersion) {
        this.appVersion = appVersion;
    }

    public RespUserStatus(@NotNull final String appVersion,
                          @NotNull final String name,
                          boolean authenticated,
                          @NotNull final String role) {
        this.appVersion = appVersion;
        this.name = name;
        this.authenticated = authenticated;
        this.role = role;
    }

    public String getAppVersion() {
        return appVersion;
    }

    @JsonProperty("appVersion")
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    @JsonProperty("authenticated")
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }
}
