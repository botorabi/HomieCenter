/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class HashGeneratorTest {

    public static final String MY_VALUE = "MyValue#/?";
    public static final String MY_VALUE_MD5_HASH = "8beb450d2f73355e9e9c80b5beafaba5";

    @Test
    public void md5Creator() throws Exception {
        final String md5hash = HashGenerator.createMD5(MY_VALUE.getBytes("UTF-8"));

        assertThat(md5hash).isEqualTo(MY_VALUE_MD5_HASH);
    }
}