/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.service.comm.*;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

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

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    //! TODO restrict the access to ADMIN only

    @PostMapping("/api/user/create")
    public ResponseEntity<HomieCenterUser> create(@RequestBody ReqUserEdit userCreate) {
        HomieCenterUser newUser = new HomieCenterUser();
        newUser.setRealName(userCreate.getRealName());
        newUser.setUserName(userCreate.getUserName());
        newUser.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        newUser.setAdmin(userCreate.isAdmin());

        try {
            newUser = userRepository.save(newUser);
        }
        catch(Throwable throwable) {
            return new ResponseEntity<>(newUser, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/api/user/edit")
    public ResponseEntity<HomieCenterUser> edit(@RequestBody ReqUserEdit userEdit, Authentication authentication) {
        Optional<HomieCenterUser> storedUser = userRepository.findById(userEdit.getId());
        if (!storedUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        User principal = (User)authentication.getPrincipal();
        if (!accessingAdminOrOwner(principal, storedUser)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        HomieCenterUser user = storedUser.get();
        user.setRealName(userEdit.getRealName());
        if (!StringUtils.isNullOrEmpty(userEdit.getPassword())) {
            user.setPassword(passwordEncoder.encode(userEdit.getPassword()));
        }
        if (roleExists(principal.getAuthorities(), ROLE_ADMIN)) {
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

    private boolean roleExists(@Nullable Collection<? extends GrantedAuthority> authorities, @NotNull final String name) {
        if (authorities == null) {
            return false;
        }
        for (GrantedAuthority authority: authorities) {
            if (authority.getAuthority().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/api/user")
    public Flux<HomieCenterUser> getAll(Authentication authentication) {
        User principal = (User)authentication.getPrincipal();
        if (!roleExists(principal.getAuthorities(), ROLE_ADMIN)) {
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

        User principal = (User)authentication.getPrincipal();
        if (!accessingAdminOrOwner(principal, user)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    //! TODO restrict the access to ADMIN only

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable("id") Long id, Authentication authentication) {
        Optional<HomieCenterUser> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User principal = (User)authentication.getPrincipal();
        // avoid deleting the user him/herself
        if ((principal == null) || accessingOwner(principal, user)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    private boolean accessingAdminOrOwner(@Nullable User principal, @NotNull Optional<HomieCenterUser> user) {
        if (principal == null) {
            return false;
        }
        if (principal.getUsername().equals(user.get().getUserName())) {
            return true;
        }
        Optional<HomieCenterUser> foundUser = userRepository.findByUserName(principal.getUsername());
        if (foundUser.isPresent() && foundUser.get().isAdmin()) {
            return true;
        }
        return false;
    }

    private boolean accessingOwner(@NotNull User principal, @NotNull Optional<HomieCenterUser> user) {
        Optional<HomieCenterUser> foundUser = userRepository.findByUserName(principal.getUsername());
        if (foundUser.isPresent() && foundUser.get().getId() == user.get().getId()) {
            return true;
        }
        return false;
    }

    @GetMapping("/api/user/status")
    public Mono<RespUserStatus> getStatus(Mono<Authentication> authentication) {
        return authentication
                .map(a -> new RespUserStatus(((User) a.getPrincipal()).getUsername(),
                        a.isAuthenticated(),
                        roleExists(a.getAuthorities(), ROLE_ADMIN) ? "ADMIN" : "USER"
                        )
                )
                .switchIfEmpty(Mono.just(new RespUserStatus()));
    }
}
