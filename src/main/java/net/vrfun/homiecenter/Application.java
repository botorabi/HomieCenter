/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.config.EnableWebFlux;


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
    CameraProxyRoutes cameraProxyRoutes;

    public Application() {
    }

    /**
     * Put all start-up code to this method.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
 //       cameraProxyRoutes.buildRoutes();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
