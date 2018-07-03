/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.HomieCenterUser;
import net.vrfun.homiecenter.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

/**
 * Utilities for checking the requesting user.
 *
 * @author          boto
 * Creation Date    3th July 2018
 */
@Component
public class AccessUtils {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private UserRepository userRepository;

    @Autowired
    public AccessUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean requestingUserIsAdminOrOwner(@Nullable Authentication authentication, @NotNull Optional<HomieCenterUser> user) {
        User principal = (User)authentication.getPrincipal();
        if (principal.getUsername().equals(user.get().getUserName())) {
            return true;
        }
        Optional<HomieCenterUser> foundUser = userRepository.findByUserName(principal.getUsername());
        return foundUser.isPresent() && foundUser.get().isAdmin();
    }

    public boolean requestingUserIsOwner(@NotNull Authentication authentication, @NotNull Optional<HomieCenterUser> user) {
        User principal = (User)authentication.getPrincipal();
        Optional<HomieCenterUser> foundUser = userRepository.findByUserName(principal.getUsername());
        return foundUser.isPresent() && foundUser.get().getId() == user.get().getId();
    }

    public boolean requestingUserIsAdmin(@NotNull Authentication authentication) {
        User principal = (User)authentication.getPrincipal();
        return roleExists(principal.getAuthorities(), ROLE_ADMIN);
    }

    public boolean roleExists(@Nullable Collection<? extends GrantedAuthority> authorities, @NotNull final String name) {
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
