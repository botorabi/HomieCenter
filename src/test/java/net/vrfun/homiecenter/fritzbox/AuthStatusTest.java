/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class AuthStatusTest {

    private AuthStatus authStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        authStatus = new AuthStatus();
    }

    @Test
    public void validSID() {
        authStatus.setSID("42");

        assertThat(authStatus.hasValidSID()).isTrue();
    }

    @Test
    public void noValidSIDEmptyString() {
        authStatus.setSID("");

        assertThat(authStatus.hasValidSID()).isFalse();
    }

    @Test
    public void noValidSIDNumberIsNull() {
        authStatus.setSID("0");

        assertThat(authStatus.hasValidSID()).isFalse();
    }

    @Test
    public void authenticated() {
        authStatus.setSID("My SID - 1042");

        assertThat(authStatus.isAuthenticated()).isTrue();
    }

    @Test
    public void notAuthenticated() {
        authStatus.setSID("");

        assertThat(authStatus.isAuthenticated()).isFalse();
    }

    @Test
    public void testToString() {
        assertThat(authStatus.toString()).contains("SID");

        Map<String, String> rights = new HashMap<>();
        rights.put("LIST_DEVS", "YES");
        rights.put("EMPTY_VALUE", "");
        authStatus.setRights(rights);

        assertThat(authStatus.toString()).contains("LIST_DEVS");
    }
}