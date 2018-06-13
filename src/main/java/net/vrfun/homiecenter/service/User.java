/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.fritzbox.*;
import net.vrfun.homiecenter.service.comm.ReqLogin;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


/**
 * A REST service providing user authentication.
 *
 * @author          boto
 * Creation Date    8th June 2018
 */
@RestController
public class User {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FRITZBox fritzBox;

    @PostMapping("/user/login")
    public ResponseEntity<AuthStatus> loginUser(@RequestBody ReqLogin reqLogin) throws Exception {
        if (fritzBox.getAuthStatus().isAuthenticated()) {
            fritzBox.logout();
        }

        try {
            AuthStatus authStatus = fritzBox.login(reqLogin.getLogin(), reqLogin.getPassword());
            LOGGER.debug("local user {} successfully logged in", reqLogin.getLogin());
            return new ResponseEntity<>(authStatus, HttpStatus.OK);
        }
        catch(Exception exception) {
            LOGGER.debug("could not login the user {}, reason: ", reqLogin.getLogin(), exception.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/user/logout")
    public ResponseEntity<AuthStatus> logoutUser() throws Exception {
        fritzBox.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/status")
    public ResponseEntity<AuthStatus> status() throws Exception {
        fritzBox.getAuthStatus();
        return new ResponseEntity<>(fritzBox.getAuthStatus(), HttpStatus.OK);
    }
}
