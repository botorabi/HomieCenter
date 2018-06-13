/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service.comm;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for user login.
 *
 * @author          boto
 * Creation Date    8th June 2018
 */
public class ReqLogin {

    private String login;

    private String password;

    public ReqLogin() {}

    public String getLogin() {
        return login;
    }

    @JsonProperty("login")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
}
