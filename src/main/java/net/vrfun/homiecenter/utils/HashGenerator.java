/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

    private final static Logger LOGGER = LoggerFactory.getLogger(HashGenerator.class);

    @NonNull
    static public String createMD5(@NonNull final byte[] content) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content);
            byte data[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                String hex = Integer.toHexString(0xff & data[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException ex) {
            LOGGER.error("Problem occurred while creating an MD5 hash, reason: {}", ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }
}
