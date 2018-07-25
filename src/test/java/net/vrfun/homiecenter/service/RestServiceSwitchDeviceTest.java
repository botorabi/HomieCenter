/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.fritzbox.FRITZBox;
import net.vrfun.homiecenter.model.DeviceInfo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class RestServiceSwitchDeviceTest {

    private RestServiceSwitchDevice restServiceSwitchDevice;

    @Mock
    private FRITZBox fritzBox;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        restServiceSwitchDevice = new RestServiceSwitchDevice(fritzBox);
    }

    @Test
    public void getDevices() {
        ResponseEntity<List<DeviceInfo>> response = restServiceSwitchDevice.getDevices();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getDevicesWithException() {
        try {
            when(fritzBox.getDevices()).thenThrow(new Exception(""));
        }
        catch(Exception ex) {}

        ResponseEntity<List<DeviceInfo>> response = restServiceSwitchDevice.getDevices();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDevice() {
        try {
            when(fritzBox.getDevice(any())).thenReturn(new DeviceInfo());
        }
        catch(Exception ex) {}

        ResponseEntity<DeviceInfo> response = restServiceSwitchDevice.getDevice(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getDeviceWithException() {
        try {
            when(fritzBox.getDevice(anyLong())).thenThrow(new Exception(""));
        }
        catch(Exception ex) {}

        ResponseEntity<DeviceInfo> response = restServiceSwitchDevice.getDevice(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void executeCommand() {
        ResponseEntity<Void> response = restServiceSwitchDevice.executeCommand(42L, "cmd");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void executeCommandWithException() {
        try {
            doThrow(new Exception("")).when(fritzBox).handleDeviceCommand(anyLong(), anyString());
        }
        catch(Exception ex) {}

        ResponseEntity<Void> response = restServiceSwitchDevice.executeCommand(42L, "cmd");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }
}