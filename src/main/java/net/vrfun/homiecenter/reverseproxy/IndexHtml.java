/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import org.slf4j.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * Using spring cloud gateway prevents obviously the automatic mapping of root directory to index.html.
 * So we have to do that manually here.
 *
 *  TODO replace this solution by a proper rewrite filter!
 *
 * @author          boto
 * Creation Date    25th June 2018
 */
@Controller
public class IndexHtml {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final String INDEX_HTML_FILE = "static/index.html";

    private static String content;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> home() throws IOException {
        return new ResponseEntity<>(getIndexHtmlContent(), HttpStatus.OK);
    }

    private String getIndexHtmlContent() throws IOException {
        if (content != null) {
            return content;
        }

        ClassPathResource resource = new ClassPathResource(INDEX_HTML_FILE);
        try {
            content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            LOGGER.error("Could not get static resource index.html");
            throw ex;
        }
        return content;
    }
}
