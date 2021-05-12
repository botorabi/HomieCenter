/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * All start-up tasks are gathered here.
 *
 * @author          boto
 * Creation Date    6th July 2018
 */
@Component
public class ApplicationStartup {

    private final CameraProxyRoutes cameraProxyRoutes;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationStartup(@NonNull final CameraProxyRoutes cameraProxyRoutes,
                              @NonNull final UserRepository userRepository,
                              @NonNull final PasswordEncoder passwordEncoder) {
        this.cameraProxyRoutes = cameraProxyRoutes;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationStartup createApplicationStartup(@NonNull final CameraProxyRoutes cameraProxyRoutes,
                                                       @NonNull final UserRepository userRepository,
                                                       @NonNull final PasswordEncoder passwordEncoder) {

        return new ApplicationStartup(cameraProxyRoutes, userRepository, passwordEncoder);
    }

    public void start() {
        cameraProxyRoutes.buildRoutes();
        createDefaultUserIfNeeded();
    }

    private void createDefaultUserIfNeeded() {
        if (userRepository.count() == 0) {
            HomieCenterUser defaultUser = new HomieCenterUser();
            defaultUser.setUserName("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setRealName("Administrator");
            defaultUser.setAdmin(true);
            userRepository.save(defaultUser);
        }
    }
}
