/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.testutils;

import net.vrfun.homiecenter.model.HomieCenterUser;
import net.vrfun.homiecenter.model.UserRepository;
import net.vrfun.homiecenter.service.AccessUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


public class UserTestUtils {

    private UserRepository userRepository;

    public UserTestUtils(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void mockEmptyUserRepository() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
    }

    @NonNull
    public HomieCenterUser mockUserRepositoryWithUser(boolean admin, long id, @NonNull final String userName, @NonNull final String password) {
        HomieCenterUser user = new HomieCenterUser();
        user.setId(id);
        user.setAdmin(admin);
        user.setRealName(userName);
        user.setUserName(userName);
        user.setPassword(password);

        when(userRepository.findByUserName(eq(userName))).thenReturn(Optional.of(user));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        return user;
    }

    @NonNull
    public Authentication createAuthentication(@NonNull final String userName, boolean isAdmin) {
        User user = new User(userName, "password",
                isAdmin ? Arrays.asList(new SimpleGrantedAuthority(AccessUtils.ROLE_ADMIN)) : Collections.emptyList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "password");

        return authentication;
    }

    @NonNull
    public Optional<HomieCenterUser> createUser(@NonNull final String userName) {
        HomieCenterUser user = new HomieCenterUser();
        user.setUserName(userName);
        return Optional.of(user);
    }
}
