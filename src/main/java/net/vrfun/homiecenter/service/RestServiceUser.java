/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.ApplicationProperties;
import net.vrfun.homiecenter.model.HomieCenterUser;
import net.vrfun.homiecenter.model.UserRepository;
import net.vrfun.homiecenter.service.comm.ReqUserEdit;
import net.vrfun.homiecenter.service.comm.RespUserStatus;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;


/**
 * A REST service providing user authentication.
 *
 * @author          boto
 * Creation Date    8th June 2018
 */
@RestController
public class RestServiceUser {

    private final Logger LOGGER = LoggerFactory.getLogger(RestServiceUser.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccessUtils accessUtils;

    private final ApplicationProperties applicationProperties;

    @Autowired
    public RestServiceUser(@NonNull final UserRepository userRepository,
                           @NonNull final PasswordEncoder passwordEncoder,
                           @NonNull final AccessUtils accessUtils,
                           @NonNull final ApplicationProperties applicationProperties) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessUtils = accessUtils;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Access is restricted to admin user.
     */
    @PostMapping("/api/user/create")
    public ResponseEntity<HomieCenterUser> create(@RequestBody ReqUserEdit userCreate, Authentication authentication) {
        //! NOTE: Currently, we cannot easily use the @Secured annotation in cloud gateway, so we check the access manually.
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (!validateNewUser(userCreate)) {
            LOGGER.info("Cannot create new user, invalid login name: {}", userCreate.getUserName());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        HomieCenterUser newUser;
        try {
            newUser = createNewUser(userCreate);
        }
        catch(Throwable throwable) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    private boolean validateNewUser(@NonNull final ReqUserEdit newUser) {
        final String userName = StringUtils.isNullOrEmpty(newUser.getUserName()) ? "" : newUser.getUserName();
        if ((userName.length() < 5) || (!userName.toLowerCase().equals(userName))) {
            return false;
        }

        if (StringUtils.isNullOrEmpty(newUser.getPassword())) {
            return false;
        }

        return true;
    }

    private HomieCenterUser createNewUser(@NonNull final ReqUserEdit newUser) {
        HomieCenterUser homieCenterUser = new HomieCenterUser();
        homieCenterUser.setRealName(newUser.getRealName());
        homieCenterUser.setUserName(newUser.getUserName());
        homieCenterUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        homieCenterUser.setAdmin(newUser.isAdmin());

        return userRepository.save(homieCenterUser);
    }

    @PostMapping("/api/user/edit")
    public ResponseEntity<HomieCenterUser> edit(@RequestBody ReqUserEdit userEdit, Authentication authentication) {
        Optional<HomieCenterUser> storedUser = userRepository.findById(userEdit.getId());
        if (!storedUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!accessUtils.requestingUserIsAdminOrOwner(authentication, storedUser)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        HomieCenterUser user = storedUser.get();
        user.setRealName(userEdit.getRealName());
        if (!StringUtils.isNullOrEmpty(userEdit.getPassword())) {
            user.setPassword(passwordEncoder.encode(userEdit.getPassword()));
        }
        // changing the admin flag is only allowed to admins, but they are not allowed to change their own admin flag
        //  thus avoiding an accidental role degradation.
        if (accessUtils.requestingUserIsAdmin(authentication) &&
                !accessUtils.requestingUserIsOwner(authentication, storedUser)) {

            user.setAdmin(userEdit.isAdmin());
        }

        try {
            user = userRepository.save(user);
        }
        catch(Throwable throwable) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/api/user")
    public Flux<HomieCenterUser> getAll(Authentication authentication) {
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            User principal = (User)authentication.getPrincipal();
            Optional<HomieCenterUser> currentUser = userRepository.findByUserName(principal.getUsername());
            return Flux.just(currentUser.get());
        }
        return Flux.fromIterable(userRepository.findAll());
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<HomieCenterUser> getById(@PathVariable("id") Long id, Authentication authentication) {
        Optional<HomieCenterUser> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!accessUtils.requestingUserIsAdminOrOwner(authentication, user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    /**
     * Access is restricted to admin user.
     */
    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable("id") Long id, Authentication authentication) {
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<HomieCenterUser> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // avoid deleting the user him/herself
        if (accessUtils.requestingUserIsOwner(authentication, user)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/api/user/status")
    public Mono<RespUserStatus> getStatus(Mono<Authentication> authentication) {
        return authentication
                .map(a -> new RespUserStatus(
                        applicationProperties.getAppVersion(),
                        ((User) a.getPrincipal()).getUsername(),
                        a.isAuthenticated(),
                        accessUtils.requestingUserIsAdmin(a) ? "ADMIN" : "USER"
                        )
                )
                .switchIfEmpty(Mono.just(new RespUserStatus(applicationProperties.getAppVersion())));
    }
}
