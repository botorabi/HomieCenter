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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;


/**
 * Main application class
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@SpringBootApplication
public class Application {

    final private ApplicationStartup applicationStartup;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    public Application(@NonNull final ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    /**
     * Put all start-up code to this method.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        applicationStartup.start();
    }
}
