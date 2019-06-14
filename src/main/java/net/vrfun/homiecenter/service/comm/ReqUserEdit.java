/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service.comm;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for editing an existing or creating a new user.
 *
 * @author          boto
 * Creation Date    2nd July 2018
 */
public class ReqUserEdit {

    private long id;

    private String realName;

    private String userName;

    private String password;

    private boolean admin;


    public ReqUserEdit() {}

    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    @JsonProperty("realName")
    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    @JsonProperty("admin")
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
