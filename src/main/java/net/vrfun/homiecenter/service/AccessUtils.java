/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Utilities for checking the requesting user.
 *
 * @author          boto
 * Creation Date    3th July 2018
 */
@Component
public class AccessUtils {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;

    @Autowired
    public AccessUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean requestingUserIsAdminOrOwner(@NonNull Authentication authentication, @NonNull Optional<HomieCenterUser> userInRepository) {
        final User principal = (User)authentication.getPrincipal();
        if (principal.getUsername().equals(userInRepository.get().getUserName())) {
            return true;
        }
        final Optional<HomieCenterUser> foundUser = userRepository.findByUserName(principal.getUsername());
        return foundUser.isPresent() && foundUser.get().isAdmin();
    }

    public boolean requestingUserIsOwner(@NonNull Authentication authentication, @NonNull Optional<HomieCenterUser> userInRepository) {
        final User principal = (User)authentication.getPrincipal();
        return userInRepository.isPresent() && userInRepository.get().getUserName().equals(principal.getUsername());
    }

    public boolean requestingUserIsAdmin(@NonNull Authentication authentication) {
        final User principal = (User)authentication.getPrincipal();
        return roleExists(principal.getAuthorities(), ROLE_ADMIN);
    }

    public boolean roleExists(@Nullable Collection<? extends GrantedAuthority> authorities, @NonNull final String name) {
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
}
