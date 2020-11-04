/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.ApplicationProperties;
import net.vrfun.homiecenter.model.DeviceStats;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class FRITZBoxTest {

    private FRITZBox fritzBox;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private FritzBoxAuthentication fritzBoxAuthentication;

    @Mock
    private Requests requests;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        fritzBox = new FRITZBox(applicationProperties);
        fritzBox
                .withRequests(requests)
                .withFritzBoxAuthentication(fritzBoxAuthentication)
                .withResponseHandlerDeviceList(new ResponseHandlerDeviceList())
                .withResponseHandlerDeviceStats(new ResponseHandlerDeviceStats());

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

    private void mockFritzBoxUrl(@NonNull final String url) {
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
    public void loginNonCached() throws Exception {
        AuthStatus authStatus = new AuthStatus();
        authStatus.setSID("");
        doReturn(authStatus).when(fritzBox).getCachedAuthStatus();

        doReturn(authStatus).when(fritzBox).login(any(), any());

        fritzBox.loginIfNeeded();
        verify(fritzBox).login(any(), any());
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
        mockFritzBoxWithDeviceResponse("<devicelist></devicelist>", HttpStatus.OK);

        assertThat(fritzBox.getDevices()).isEmpty();
    }

    @Test
    public void getDevicesWithDevices() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevices().size()).isEqualTo(2);
    }

    @Test
    public void getDevicesWithDevicesHttpError() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.NOT_FOUND);

        assertThat(fritzBox.getDevices().size()).isEqualTo(0);
    }

    private void mockFritzBoxWithDeviceResponse(@NonNull final String responseBody, HttpStatus status) throws Exception {
        mockFritzBoxAuthentication(true);
        mockFritzBoxUrl("http://fritz.box");

        ResponseEntity<String> response = new ResponseEntity<>(responseBody, status);
        doReturn(response).when(fritzBox).requestHttpGET(anyString(), anyString(), anyMap());
    }

    @Test
    public void getDeviceNotExisting() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevice(0)).isNull();
    }

    @Test
    public void getDevice() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThat(fritzBox.getDevice(16)).isNotNull();
    }

    @Test
    public void getDeviceStats() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceStatsTest.XML_INPUT, HttpStatus.OK);

        DeviceStats deviceStats = fritzBox.getDeviceStats("MyAIN");
        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getTemperature().getStats().size()).isNotZero();
        assertThat(deviceStats.getPower().getStats().size()).isNotZero();
        assertThat(deviceStats.getEnergy().getStats().size()).isNotZero();
    }

    @Test
    public void handleSwitchCommand() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        fritzBox.handleDeviceCommand(16, "on");
        fritzBox.handleDeviceCommand(16, "off");
    }

    @Test
    public void handleInvalidSwitchCommand() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_TWO_SWITCH_DEVICES, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(16, "invalid_cmd")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleSwitchCommandInvalidDeviceType() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_THREE_MIXED_DEVICES, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(0, "on")).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "on")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleHeatControllerCommand() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_ONE_HC_DEVICE, HttpStatus.OK);

        fritzBox.handleDeviceCommand(20, "temperature=32");
    }

    @Test
    public void handleInvalidHeatControllerCommand() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_ONE_HC_DEVICE, HttpStatus.OK);

        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "temperature=")).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> fritzBox.handleDeviceCommand(20, "temperature=NotANumber")).isInstanceOf(Exception.class);
    }

    @Test
    public void handleHeatControllerCommandInvalidDeviceType() throws Exception {
        mockFritzBoxWithDeviceResponse(ResponseHandlerDeviceListTest.XML_INPUT_THREE_MIXED_DEVICES, HttpStatus.OK);

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