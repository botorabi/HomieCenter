/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
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

    @Autowired
    private CameraProxyRoutes cameraProxyRoutes;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationStartup createApplicationStartup() {
        return new ApplicationStartup();
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
