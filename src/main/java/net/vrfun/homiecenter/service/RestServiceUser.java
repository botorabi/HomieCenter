/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.fritzbox.*;
import net.vrfun.homiecenter.service.comm.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * A REST service providing user authentication.
 *
 * @author          boto
 * Creation Date    8th June 2018
 */
@RestController
public class RestServiceUser {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Virtual role for successfully authenticated users
     */
    public static final String ROLE_USER = "ROLE_USER";

    @Autowired
    FRITZBox fritzBox;

    @PostMapping("/user/login")
    public ResponseEntity<AuthStatus> loginUser(@RequestBody ReqLogin reqLogin) throws Exception {
        if (fritzBox.getAuthStatus().isAuthenticated()) {
            logoutUser();
        }

        try {
            AuthStatus authStatus = fritzBox.login(reqLogin.getLogin(), reqLogin.getPassword());
            LOGGER.debug("local user {} successfully logged in", reqLogin.getLogin());
            authorizeUser(reqLogin.getLogin());
            return new ResponseEntity<>(authStatus, HttpStatus.OK);
        }
        catch(Exception exception) {
            LOGGER.debug("could not login the user {}, reason: ", reqLogin.getLogin(), exception.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/user/logout")
    public ResponseEntity<AuthStatus> logoutUser() throws Exception {
        deauthorizeUser();
        fritzBox.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/status")
    public ResponseEntity<RespUser> status() throws Exception {
        boolean isAuthenticated = false;
        String userName = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (checkUserRole(ROLE_USER, authentication)) {
            isAuthenticated = fritzBox.getAuthStatus().isAuthenticated();
            if (isAuthenticated) {
                userName = (String) authentication.getPrincipal();
            }
        }

        return new ResponseEntity<>(new RespUser(userName, isAuthenticated), HttpStatus.OK);
    }

    private void authorizeUser(@NotNull final String userName) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void deauthorizeUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public boolean checkUserRole(@NotNull final String roleName, @Nullable final Authentication authentication) {
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (roleName.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }
}
