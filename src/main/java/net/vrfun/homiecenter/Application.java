/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Main application class
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    @Autowired
    private CameraProxyRoutes cameraProxyRoutes;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Application() {
    }

    /**
     * Put all start-up code to this method.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
