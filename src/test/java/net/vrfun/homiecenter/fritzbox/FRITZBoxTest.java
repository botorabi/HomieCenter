/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class FRITZBoxTest {

    private FRITZBox fritzBox;

    @Mock
    private FritzBoxAuthentication fritzBoxAuthentication;

    @Mock
    private ResponseHandlerDeviceList responseHandlerDeviceList;

    @Mock
    private Requests requests;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fritzBox = new FRITZBox();
        fritzBox
                .withRequests(requests)
                .withFritzBoxAuthentication(fritzBoxAuthentication)
                .withResponseHandlerSwitchDeviceList(responseHandlerDeviceList);
    }

    @Test
    public void getCachedAuthStatus() throws Exception {
        mockFrithBoxAuthentication(true);

        assertThat(fritzBox.getCachedAuthStatus().isAuthenticated()).isTrue();
    }

    private void mockFrithBoxAuthentication(boolean authenticated) throws Exception {
        AuthStatus authStatus = new AuthStatus();
        authStatus.setSID(authenticated ? "My SID" : "");

        doReturn(authStatus).when(fritzBoxAuthentication).getAuthStatus();
        doReturn(authStatus).when(fritzBoxAuthentication).login(any(), any());
    }

    @Test
    public void getAuthStatus() throws Exception {
        mockFrithBoxAuthentication(false);

        assertThat(fritzBox.getAuthStatus().isAuthenticated()).isFalse();
    }

    @Test
    public void loginSuccess() throws Exception {
        mockFrithBoxAuthentication(true);

        assertThat(fritzBox.login("username", "password").isAuthenticated()).isTrue();
    }

    @Test
    public void loginFail() throws Exception {
        mockFrithBoxAuthentication(false);

        assertThatThrownBy(() ->fritzBox.login("username", "password")).isInstanceOf(Exception.class);
    }

    @Test
    public void logout() throws Exception {
        mockFrithBoxAuthentication(true);

        fritzBox.logout();
    }
}