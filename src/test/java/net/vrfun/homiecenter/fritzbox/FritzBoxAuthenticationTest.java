/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.utils.HashGenerator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class FritzBoxAuthenticationTest {

    private FritzBoxAuthentication fritzBoxAuthentication;

    @Mock
    private ResponseHandlerAuthStatus responseHandlerAuthStatus;

    @Mock
    private Requests requests;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        FritzBoxAuthentication authenticator = new FritzBoxAuthentication();
        authenticator
                .withFritzBoxAddress("http://fritz.box")
                .withResponseHandlerAuthStatus(responseHandlerAuthStatus)
                .withRequests(requests);

        fritzBoxAuthentication = spy(authenticator);
    }

    @Test
    public void authStatusSuccess() throws Exception {
        mockGetAuthentication("My SID", HttpStatus.OK);

        assertThat(fritzBoxAuthentication.getAuthStatus().isAuthenticated()).isTrue();
    }

    private void mockGetAuthentication(@NotNull final String SID, @NotNull HttpStatus status) throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(status);
        doReturn(response).when(fritzBoxAuthentication).getConnectionState();

        doAnswer((invocation) -> {
            final AuthStatus authStatus = invocation.getArgument(1);
            authStatus.setSID(SID);
            return null;

        }).when(responseHandlerAuthStatus).read(any(), any());
    }

    @Test
    public void authStatusNoAccess() throws Exception {
        mockGetAuthentication("", HttpStatus.NOT_FOUND);

        assertThatThrownBy(() ->fritzBoxAuthentication.getAuthStatus()).isInstanceOf(Exception.class);
    }

    @Test
    public void authStatusForbiddenAccess() throws Exception {
        mockGetAuthentication("", HttpStatus.FORBIDDEN);

        assertThat(fritzBoxAuthentication.getAuthStatus().isAuthenticated()).isFalse();
    }

    @Test
    public void authStatusConnectionException() throws Exception {
        doThrow(new Exception("No Connection")).when(fritzBoxAuthentication).getConnectionState();

        assertThatThrownBy(() -> fritzBoxAuthentication.getAuthStatus()).isInstanceOf(Exception.class);
    }

    @Test
    public void authStatusInvalidResponseFormat() throws Exception {
        doThrow(new Exception("Invalid Response Format")).when(responseHandlerAuthStatus).read(anyString(), any());

        assertThatThrownBy(() -> fritzBoxAuthentication.getAuthStatus()).isInstanceOf(Exception.class);
    }

    @Test
    public void loginAlreadyAuthenticated() throws Exception {
        mockGetAuthentication("My SID", HttpStatus.OK);

        assertThat(fritzBoxAuthentication.login("username", "password").isAuthenticated()).isTrue();
    }

    @Test
    public void loginWithExceptionOnAuthentication() throws Exception {
        mockGetAuthentication("", HttpStatus.OK);

        doThrow(new Exception("Cannot authenticate")).when(fritzBoxAuthentication).authenticate(any(), any(), any());

        assertThatThrownBy(() -> fritzBoxAuthentication.login("username", "password")).isInstanceOf(Exception.class);
    }

    @Test
    public void loginSuccess() throws Exception {
        mockGetAuthentication("", HttpStatus.OK);

        AuthStatus authStatus = new AuthStatus();
        authStatus.setSID("My SID");
        doReturn(authStatus).when(fritzBoxAuthentication).authenticate(any(), any(), any());

        assertThat(fritzBoxAuthentication.login("username", "password").isAuthenticated()).isTrue();
    }

    @Test
    public void logoutSuccess() {
        try {
            mockGetAuthentication("My SID", HttpStatus.OK);

            fritzBoxAuthentication.logout();
        }
        catch(Exception exception) {
            fail("Logout failed!");
        }
    }

    @Test
    public void logoutNotAuthenticatedBefore() {
        try {
            mockGetAuthentication("", HttpStatus.OK);

            fritzBoxAuthentication.logout();
        }
        catch(Exception exception) {
            fail("Logout failed!");
        }
    }

    @Test
    public void logoutWithException() throws Exception {
        mockGetAuthentication("My SID", HttpStatus.OK);

        doThrow(new Exception("Cannot authenticate")).when(requests).post(any(), any());

        assertThatThrownBy(() -> fritzBoxAuthentication.logout()).isInstanceOf(Exception.class);
    }

    @Test
    public void authenticate() throws Exception {
        mockGetAuthentication("My SID", HttpStatus.OK);

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        doReturn(response).when(requests).get(any(), any());

        fritzBoxAuthentication.authenticate("challenge", "username", "password");
    }

    @Test
    public void authenticateFRITZBoxConnectionProblem() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        doReturn(response).when(requests).get(anyString(), anyMap());

        assertThatThrownBy(() -> fritzBoxAuthentication.authenticate("", "", "")).isInstanceOf(Exception.class);
    }

    @Test
    public void authenticateWithInvalidSID() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        doReturn(response).when(requests).get(anyString(), anyMap());

        AuthStatus result = fritzBoxAuthentication.authenticate("", "", "");

        assertThat(result.hasValidSID()).isFalse();
    }

    @Test
    public void connectionStateWithException() throws Exception {
        doThrow(new Exception()).when(requests).get(any(), any());

        assertThatThrownBy(() -> fritzBoxAuthentication.getConnectionState()).isInstanceOf(Exception.class);
    }

    @Test
    public void preparePassword() {
        assertThat(fritzBoxAuthentication.preparePassword("My Über Passwärt")).isEqualTo("My .ber Passw.rt");
    }

    @Test
    public void createResponse() throws Exception {
        final String challenge = "The Response Challenge";
        final String password = "My Password";
        final String expectedResponse = challenge + "-" + HashGenerator.createMD5(
                (challenge + "-" + fritzBoxAuthentication.preparePassword(password)).getBytes("UTF-16LE"));

        assertThat(fritzBoxAuthentication.createResponse(challenge, password)).isEqualTo(expectedResponse);
    }
}
