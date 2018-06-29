/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.DeviceInfo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ResponseHandlerSwitchDeviceListTest {

    private ResponseHandlerSwitchDeviceList responseHandlerSwitchDeviceList;

    private static final String XML_INPUT_TWO_DEVICES =
                    "<devicelist version=\"1\">" +
                    "  <device identifier=\"08761 0475688\" id=\"16\" functionbitmask=\"2944\" fwversion=\"04.06\" manufacturer=\"AVM\" productname=\"FRITZ!DECT 200\">" +
                    "    <present>1</present>" +
                    "    <name>Küche</name>" +
                    "    <switch>" +
                    "      <state>1</state>" +
                    "      <mode>manuell</mode>" +
                    "      <lock>0</lock>" +
                    "      <devicelock>0</devicelock>" +
                    "    </switch>" +
                    "    <powermeter>" +
                    "      <voltage>228956</voltage>" +
                    "      <power>10</power>" +
                    "      <energy>20</energy>" +
                    "    </powermeter>" +
                    "    <temperature>" +
                    "       <celsius>310</celsius>" +
                    "       <offset>5</offset>" +
                    "    </temperature>" +
                    "  </device>" +
                    "  <device identifier=\"08761 0475689\" id=\"17\" functionbitmask=\"2944\" fwversion=\"04.06\" manufacturer=\"AVM\" productname=\"FRITZ!DECT 200\">" +
                    "    <present>1</present>" +
                    "    <name>FRITZ!DECT 200 #2</name>" +
                    "    <switch>" +
                    "      <state></state>" +
                    "      <mode>manuell</mode>" +
                    "      <lock></lock>" +
                    "      <devicelock>0</devicelock>" +
                    "    </switch>" +
                    "    <powermeter>" +
                    "      <voltage>228956</voltage>" +
                    "      <power>100</power>" +
                    "      <energy>200</energy>" +
                    "    </powermeter>" +
                    "    <temperature>" +
                    "       <celsius>315</celsius>" +
                    "       <offset>10</offset>" +
                    "    </temperature>" +
                    "  </device>" +
                    "</devicelist>";

    @Before
    public void setup() {
        responseHandlerSwitchDeviceList = new ResponseHandlerSwitchDeviceList();
    }

    private Document createDocument(final String content) {
        try {
            return responseHandlerSwitchDeviceList.parseResponse(content);
        }
        catch(Throwable throwable) {
        }
        return null;
    }

    @Test
    public void invalidFormat() {
        assertThat(createDocument("")).isNull();
        assertThat(createDocument("<invalid-tag>")).isNull();
    }

    @Test
    public void readModel() throws Exception {
        List<DeviceInfo> deviceInfoList = readInput(false, XML_INPUT_TWO_DEVICES);

        assertThat(deviceInfoList.size()).isEqualTo(2);

        DeviceInfo deviceInfo1 = deviceInfoList.get(0);
        assertThat(deviceInfo1.isOn()).isEqualTo(true);
        assertThat(deviceInfo1.getAIN()).isEqualTo("08761 0475688");
        assertThat(deviceInfo1.getId()).isEqualTo("16");
        assertThat(deviceInfo1.getName()).isEqualTo("Küche");
        assertThat(deviceInfo1.getProductName()).isEqualTo("FRITZ!DECT 200");
        assertThat(deviceInfo1.getFirmware()).isEqualTo("04.06");
        assertThat(deviceInfo1.getEnergy()).isEqualTo(20);
        assertThat(deviceInfo1.getPower()).isEqualTo(10);
        assertThat(deviceInfo1.getVoltage()).isEqualTo(228956);
        assertThat(deviceInfo1.getTemperature()).isEqualTo(310);
        assertThat(deviceInfo1.getTemperatureOffset()).isEqualTo(5);

        DeviceInfo deviceInfo2 = deviceInfoList.get(1);
        assertThat(deviceInfo2.isOn()).isEqualTo(false);
        assertThat(deviceInfo2.getAIN()).isEqualTo("08761 0475689");
        assertThat(deviceInfo2.getId()).isEqualTo("17");
        assertThat(deviceInfo2.getName()).isEqualTo("FRITZ!DECT 200 #2");
        assertThat(deviceInfo2.getProductName()).isEqualTo("FRITZ!DECT 200");
        assertThat(deviceInfo2.getFirmware()).isEqualTo("04.06");
        assertThat(deviceInfo2.getEnergy()).isEqualTo(200);
        assertThat(deviceInfo2.getPower()).isEqualTo(100);
        assertThat(deviceInfo2.getVoltage()).isEqualTo(228956);
        assertThat(deviceInfo2.getTemperature()).isEqualTo(315);
        assertThat(deviceInfo2.getTemperatureOffset()).isEqualTo(10);
    }

    private List<DeviceInfo> readInput(final boolean caseSensitive, @NotNull final String inputString) throws Exception {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        responseHandlerSwitchDeviceList.setUseCaseSensitiveNames(caseSensitive);
        responseHandlerSwitchDeviceList.read(inputString, deviceInfoList);
        return deviceInfoList;
    }
}
