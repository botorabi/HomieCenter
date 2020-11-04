/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;

import net.vrfun.homiecenter.model.HomieCenterUser;
import net.vrfun.homiecenter.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * This service is used for authentication process.
 *
 * @author          boto
 * Creation Date    4th July 2018
 */
@Component
public class UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public ReactiveUserDetailsService createUserDetailsService() {
        return userName -> {
            Optional<HomieCenterUser> user = userRepository.findByUserName(userName);

            if (!user.isPresent()) {
                return Mono.empty();
            }

            UserDetails userDetails = User.builder()
                    .username(user.get().getUserName())
                    .password(user.get().getPassword())
                    .roles(user.get().isAdmin() ? "ADMIN" : "USER")
                    .build();

            return Mono.just(userDetails);
        };
    }
}
