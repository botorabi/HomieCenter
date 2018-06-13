/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;


/**
 * Main application class.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
