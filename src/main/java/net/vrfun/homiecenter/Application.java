/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


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
    private ApplicationStartup applicationStartup;

    /**
     * Put all start-up code to this method.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        applicationStartup.start();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
