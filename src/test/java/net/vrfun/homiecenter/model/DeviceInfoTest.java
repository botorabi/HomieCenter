/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class DeviceInfoTest {


    private SwitchDeviceInfo switchDeviceInfo;

    private HeatControllerDeviceInfo heatControllerDeviceInfo;

    @Before
    public void setup() {
        switchDeviceInfo = new SwitchDeviceInfo();
        heatControllerDeviceInfo = new HeatControllerDeviceInfo();
    }

    @Test
    public void switchDeviceInfo() {
        switchDeviceInfo.setId("MyID");
        switchDeviceInfo.setAIN("MyAIN");

        assertThat(switchDeviceInfo.getDeviceType()).isEqualTo(SwitchDeviceInfo.DEVICE_TYPE);
        assertThat(switchDeviceInfo.toString()).contains("MyID");
        assertThat(switchDeviceInfo.toString()).contains("MyAIN");

        switchDeviceInfo.setPresent(true);
        assertThat(switchDeviceInfo.toString()).contains("present: yes");
        switchDeviceInfo.setPresent(false);
        assertThat(switchDeviceInfo.toString()).contains("present: no");
    }

    @Test
    public void heatControllerDeviceInfo() {
        heatControllerDeviceInfo.setId("MyID");
        heatControllerDeviceInfo.setAIN("MyAIN");
        heatControllerDeviceInfo.setWindowOpen(true);

        assertThat(heatControllerDeviceInfo.getDeviceType()).isEqualTo(HeatControllerDeviceInfo.DEVICE_TYPE);
        assertThat(heatControllerDeviceInfo.toString()).contains("MyID");
        assertThat(heatControllerDeviceInfo.toString()).contains("MyAIN");
        assertThat(heatControllerDeviceInfo.isWindowOpen()).isTrue();

        heatControllerDeviceInfo.setPresent(true);
        assertThat(heatControllerDeviceInfo.toString()).contains("present: yes");
        heatControllerDeviceInfo.setPresent(false);
        assertThat(heatControllerDeviceInfo.toString()).contains("present: no");
    }
}