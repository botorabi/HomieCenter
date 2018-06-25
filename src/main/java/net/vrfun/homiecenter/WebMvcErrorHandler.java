/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Redirect the client to root path if a resource was not found.
 * For a single page Angular app this is necessary in order to avoid
 * trouble with direct visiting Angular's Router paths.
 *
 * @author          boto
 * Creation Date    15th June 2018
 */
@RestController
public class WebMvcErrorHandler implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    void error(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}