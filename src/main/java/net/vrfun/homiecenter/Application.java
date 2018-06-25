/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * Main application class
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@EnableAutoConfiguration
@SpringBootApplication
@Controller
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
        cameraProxyRoutes.buildRoutes();
    }

    /**
     * Using spring cloud gateway prevents us obviously to map the root directory to index.html.
     * So we have to do that manually here.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> home() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
