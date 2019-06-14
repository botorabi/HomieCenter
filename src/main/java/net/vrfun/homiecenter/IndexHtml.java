/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import net.vrfun.homiecenter.utils.StaticResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Using spring cloud gateway prevents obviously the automatic mapping of root directory to index.html.
 * So we have to do that manually here.
 *
 * @author          boto
 * Creation Date    25th June 2018
 */
@Controller
public class IndexHtml {

    private static final String INDEX_HTML_FILE = "static/index.html";

    private static String content;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> home() {
        return new ResponseEntity<>(getIndexHtmlContent(), HttpStatus.OK);
    }

    /**
     * Make Angular's router happy.
     */
    @RequestMapping(value = "/nav/**", method = RequestMethod.GET)
    public ResponseEntity<String> angularPaths() {
        return new ResponseEntity<>(getIndexHtmlContent(), HttpStatus.OK);
    }

    protected String getIndexHtmlContent() {
        if (content != null) {
            return content;
        }

        StaticResourceLoader staticResourceLoader = new StaticResourceLoader();
        content = staticResourceLoader.getTextResource(INDEX_HTML_FILE);

        return content;
    }
}
