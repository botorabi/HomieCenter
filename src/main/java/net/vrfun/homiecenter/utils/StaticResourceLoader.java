/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.utils;

import org.slf4j.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class providing functionality for loading static resources
 *
 * @author          boto
 * Creation Date    5th September 2018
 */
public class StaticResourceLoader {

    private final Logger LOGGER = LoggerFactory.getLogger(StaticResourceLoader.class);

    @Nullable
    public String getTextResource(@NotNull final String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try {
            return new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            LOGGER.error("Could not get static resource (text): {}", resourcePath);
        }
        return null;
    }

    @Nullable
    public byte[] getBinaryResource(@NotNull final String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try {
            return FileCopyUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException ex) {
            LOGGER.error("Could not get static resource (binary): {}", resourcePath);
        }
        return null;
    }
}
