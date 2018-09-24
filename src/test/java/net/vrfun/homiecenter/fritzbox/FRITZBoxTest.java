/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.ApplicationProperties;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class FRITZBoxTest {

    private FRITZBox fritzBox;

    @Mock
    private FritzBoxAuthentication fritzBoxAuthentication;

    @Mock
    private Requests requests;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fritzBox = new FRITZBox();
        fritzBox
                .withRequests(requests)
                .withFritzBoxAuthentication(fritzBoxAuthentication)
                .withResponseHandlerDeviceList(new ResponseHandlerDeviceList());

        fritzBox = spy(fritzBox);
    }

    @Test
    public void getCachedAuthStatus() throws Exception {
        mockFritzBoxAuthentication(true);

        assertThat(fritzBox.getCachedAuthStatus().isAuthenticated()).isTrue();
    }

    private void mockFritzBoxAuthentication(boolean authenticated) throws Exception {
        AuthStatus authStatus = new AuthStatus();
        authStatus.setSID(authenticated ? "My SID" : "");

        doReturn(authStatus).when(fritzBoxAuthentication).getAuthStatus();
        doReturn(authStatus).when(fritzBoxAuthentication).login(any(), any());
    }

    private void mockFritzBoxUrl(@NotNull final String url) {
        doReturn(url).when(fritzBox).getFritzBoxURL();
    }

    @Test
    public void getAuthStatus() throws Exception {
        mockFritzBoxAuthentication(false);

        assertThat(fritzBox.getAuthStatus().isAuthenticated()).isFalse();
    }

    @Test
    public void loginSuccess() throws Exception {
        mockFritzBoxAuthentication(true);

        assertThat(fritzBox.login("username", "password").isAuthenticated()).isTrue();
    }

    @Test
    public void loginFail() throws Exception {
        mockFritzBoxAuthentication(false);

        assertThatThrownBy(() -> fritzBox.login("username", "password")).isInstanceOf(Exception.class);
    }

    @Test
    public void logout() throws Exception {
        mockFritzBoxAuthentication(true);

        fritzBox.logout();
    }

    @Test
    public void getDevicesEmptyList() throws Exception {
        mockFritzBoxWithDeviceList("<devicelist></devicelist>", HttpStatus.OK);

        assertThat(fritzBox.getDevices()).isEmpty();
    }

    @Test
    public void getDevicesWithDevices() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevices().size()).isEqualTo(2);
    }

    @Test
    public void getDevicesWithDevicesHttpError() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.NOT_FOUND);

        assertThat(fritzBox.getDevices().size()).isEqualTo(0);
    }

    private void mockFritzBoxWithDeviceList(@NotNull final String responseBody, HttpStatus status) throws Exception {
        mockFritzBoxAuthentication(true);
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        mockFritzBoxUrl("http://fritz.box");
        fritzBox.withApplicationProperties(applicationProperties);

        ResponseEntity<String> response = new ResponseEntity<>(responseBody, status);
        doReturn(response).when(fritzBox).requestHttpGET(anyString(), anyString(), anyMap());
    }

    @Test
    public void getDeviceNotExisting() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevice(0)).isNull();
    }

    @Test
    public void getDevice() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevice(16)).isNotNull();
    }

    @Test
    public void handleSwitchCommand() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        fritzBox.handleDeviceCommand(16, "on");
        fritzBox.handleDeviceCommand(16, "off");
    }

    @Test
    public void handleInvalidSwitchCommand() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(16, "invalid_cmd")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleSwitchCommandInvalidDeviceType() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_THREE_MIXED_DEVICES, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(0, "on")).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "on")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleHeatControllerCommand() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_ONE_HC_DEVICE, HttpStatus.OK);

        fritzBox.handleDeviceCommand(20, "temperature=32");
    }

    @Test
    public void handleInvalidHeatControllerCommand() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_ONE_HC_DEVICE, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "temperature=")).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "temperature=NotANumber")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleHeatControllerCommandInvalidDeviceType() throws Exception {
        mockFritzBoxWithDeviceList(ResponseHandlerDeviceListTest.XML_INPUT_THREE_MIXED_DEVICES, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(0, "temperature=32")).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(16, "temperature=32")).isInstanceOf(Exception.class);
    }

    @Test
    public void requestHttpGET() throws Exception {
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("param1", "value1");

        final String SID = "MySID";
        final String FRITZBOX_URL = "http://fritz.box";
        final String RELATIVE_URL = "myurl";

        doAnswer((invocation) -> {

            String url = invocation.getArgument(0);
            Map<String, String> urlParams = invocation.getArgument(1);

            assertThat(url).isEqualTo(FRITZBOX_URL + "/" + RELATIVE_URL);
            assertThat(urlParams).containsKey("sid");
            assertThat(urlParams.get("sid")).isEqualTo(SID);

            return new ResponseEntity<>(HttpStatus.OK);

        }).when(requests).get(anyString(), anyMap());

        mockFritzBoxUrl(FRITZBOX_URL);
        fritzBox.requestHttpGET(SID, RELATIVE_URL, urlParameters);
    }
}