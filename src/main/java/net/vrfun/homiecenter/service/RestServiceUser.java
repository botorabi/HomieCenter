/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
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
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.*;


/**
 * A REST service providing user authentication.
 *
 * @author          boto
 * Creation Date    8th June 2018
 */
@RestController
public class RestServiceUser {

    @GetMapping("/test")
    public Mono<String> greetService(Mono<Principal> principal) {
        Mono<String> response = principal
                .map(Principal::getName)
                .map(name -> "User Name: " + name);
        return response;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Virtual role for successfully authenticated users
     */
    public static final String ROLE_USER = "ROLE_USER";

    @Autowired
    FRITZBox fritzBox;

    @GetMapping("/api/user/status")
    public Mono<ResponseEntity<RespUser>> status(Mono<Authentication> auth) throws Exception {
        return auth.map(a -> {
            return new ResponseEntity<>(new RespUser("USER", a.isAuthenticated()), HttpStatus.OK);
        });
    }
}
