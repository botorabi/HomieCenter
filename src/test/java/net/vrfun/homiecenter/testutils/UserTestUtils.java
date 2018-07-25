/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.testutils;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.service.AccessUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


public class UserTestUtils {

    private UserRepository userRepository;

    public UserTestUtils(@NotNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void mockEmptyUserRepository() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
    }

    @NotNull
    public HomieCenterUser mockUserRepositoryWithUser(boolean admin, long id, @NotNull final String userName, @NotNull final String password) {
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

    @NotNull
    public Authentication createAuthentication(@NotNull final String userName, boolean isAdmin) {
        User user = new User(userName, "password",
                isAdmin ? Arrays.asList(new SimpleGrantedAuthority(AccessUtils.ROLE_ADMIN)) : Collections.emptyList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "password");

        return authentication;
    }

    @NotNull
    public Optional<HomieCenterUser> createUser(@NotNull final String userName) {
        HomieCenterUser user = new HomieCenterUser();
        user.setUserName(userName);
        return Optional.of(user);
    }
}
