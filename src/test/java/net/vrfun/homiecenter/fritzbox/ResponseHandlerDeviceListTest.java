/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ResponseHandlerDeviceListTest {

    private ResponseHandlerDeviceList responseHandlerDeviceList;

    protected static final String XML_INPUT_TWO_SWITCH_DEVICES =
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

    protected static final String XML_INPUT_ONE_HC_DEVICE =
            "<devicelist version=\"1\">" +
                    "  <device identifier=\"08761 0475656\" id=\"20\" functionbitmask=\"320\" fwversion=\"04.70\" manufacturer=\"AVM\" productname=\"FRITZ!DECT 301\">" +
                    "    <present>1</present>" +
                    "    <name>Heat Controller Room 1</name>" +
                    "    <temperature>" +
                    "       <celsius>310</celsius>" +
                    "       <offset>5</offset>" +
                    "    </temperature>" +
                    "    <hkr>" +
                    "        <tist>56</tist>" +
                    "        <tsoll>53</tsoll>" +
                    "        <absenk>32</absenk>" +
                    "        <komfort>42</komfort>" +
                    "        <lock>0</lock>" +
                    "        <devicelock>0</devicelock>" +
                    "        <errorcode>0</errorcode>" +
                    "        <batterylow>0</batterylow>" +
                    "        <windowopenactiv>0</windowopenactiv>" +
                    "        <battery>100</battery>" +
                    "        <nextchange>" +
                    "            <endperiod>1536595200</endperiod>" +
                    "            <tchange>42</tchange>" +
                    "        </nextchange>" +
                    "        <summeractive>0</summeractive>" +
                    "        <holidayactive>0</holidayactive>" +
                    "    </hkr>" +
                    "  </device>" +
                    "</devicelist>";


    protected static final String XML_INPUT_THREE_MIXED_DEVICES =
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
                    "  <device identifier=\"08761 0475656\" id=\"20\" functionbitmask=\"320\" fwversion=\"04.70\" manufacturer=\"AVM\" productname=\"FRITZ!DECT 301\">" +
                    "    <present>1</present>" +
                    "    <name>Heat Controller Room 1</name>" +
                    "    <temperature>" +
                    "       <celsius>310</celsius>" +
                    "       <offset>5</offset>" +
                    "    </temperature>" +
                    "    <hkr>" +
                    "        <tist>56</tist>" +
                    "        <tsoll>53</tsoll>" +
                    "        <absenk>32</absenk>" +
                    "        <komfort>42</komfort>" +
                    "        <lock>0</lock>" +
                    "        <devicelock>0</devicelock>" +
                    "        <errorcode>0</errorcode>" +
                    "        <batterylow>0</batterylow>" +
                    "        <windowopenactiv>0</windowopenactiv>" +
                    "        <battery>100</battery>" +
                    "        <nextchange>" +
                    "            <endperiod>1536595200</endperiod>" +
                    "            <tchange>42</tchange>" +
                    "        </nextchange>" +
                    "        <summeractive>0</summeractive>" +
                    "        <holidayactive>0</holidayactive>" +
                    "    </hkr>" +
                    "  </device>" +
                    "</devicelist>";

    @Before
    public void setup() {
        responseHandlerDeviceList = new ResponseHandlerDeviceList();
    }

    private Document createDocument(final String content) {
        try {
            return responseHandlerDeviceList.parseResponse(content);
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
    public void readModelSwitchDevice() throws Exception {
        List<DeviceInfo> switchDeviceInfoList = readInput(false, XML_INPUT_TWO_SWITCH_DEVICES);

        assertThat(switchDeviceInfoList.size()).isEqualTo(2);

        SwitchDeviceInfo switchDeviceInfo1 = (SwitchDeviceInfo)switchDeviceInfoList.get(0);

        assertThat(switchDeviceInfo1.isOn()).isEqualTo(true);
        assertThat(switchDeviceInfo1.getAIN()).isEqualTo("08761 0475688");
        assertThat(switchDeviceInfo1.getId()).isEqualTo("16");
        assertThat(switchDeviceInfo1.getName()).isEqualTo("Küche");
        assertThat(switchDeviceInfo1.getProductName()).isEqualTo("FRITZ!DECT 200");
        assertThat(switchDeviceInfo1.getFirmware()).isEqualTo("04.06");
        assertThat(switchDeviceInfo1.getEnergy()).isEqualTo(20);
        assertThat(switchDeviceInfo1.getPower()).isEqualTo(10);
        assertThat(switchDeviceInfo1.getVoltage()).isEqualTo(228956);
        assertThat(switchDeviceInfo1.getTemperature()).isEqualTo(310);
        assertThat(switchDeviceInfo1.getTemperatureOffset()).isEqualTo(5);

        SwitchDeviceInfo switchDeviceInfo2 = (SwitchDeviceInfo)switchDeviceInfoList.get(1);
        assertThat(switchDeviceInfo2.isOn()).isEqualTo(false);
        assertThat(switchDeviceInfo2.getAIN()).isEqualTo("08761 0475689");
        assertThat(switchDeviceInfo2.getId()).isEqualTo("17");
        assertThat(switchDeviceInfo2.getName()).isEqualTo("FRITZ!DECT 200 #2");
        assertThat(switchDeviceInfo2.getProductName()).isEqualTo("FRITZ!DECT 200");
        assertThat(switchDeviceInfo2.getFirmware()).isEqualTo("04.06");
        assertThat(switchDeviceInfo2.getEnergy()).isEqualTo(200);
        assertThat(switchDeviceInfo2.getPower()).isEqualTo(100);
        assertThat(switchDeviceInfo2.getVoltage()).isEqualTo(228956);
        assertThat(switchDeviceInfo2.getTemperature()).isEqualTo(315);
        assertThat(switchDeviceInfo2.getTemperatureOffset()).isEqualTo(10);
    }

    private List<DeviceInfo> readInput(final boolean caseSensitive, @NotNull final String inputString) throws Exception {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        responseHandlerDeviceList.setUseCaseSensitiveNames(caseSensitive);
        responseHandlerDeviceList.read(inputString, deviceInfoList);
        return deviceInfoList;
    }

    @Test
    public void readModelHeatController() throws Exception {
        List<DeviceInfo> heatControllerInfoList = readInput(false, XML_INPUT_ONE_HC_DEVICE);

        assertThat(heatControllerInfoList.size()).isEqualTo(1);

        HeatControllerDeviceInfo heatControllerInfo = (HeatControllerDeviceInfo) heatControllerInfoList.get(0);

        assertThat(heatControllerInfo.getAIN()).isEqualTo("08761 0475656");
        assertThat(heatControllerInfo.getId()).isEqualTo("20");
        assertThat(heatControllerInfo.getName()).isEqualTo("Heat Controller Room 1");
        assertThat(heatControllerInfo.getProductName()).isEqualTo("FRITZ!DECT 301");
        assertThat(heatControllerInfo.getFirmware()).isEqualTo("04.70");
        assertThat(heatControllerInfo.getComfortTemperature()).isEqualTo(42);
        assertThat(heatControllerInfo.getEconomyTemperature()).isEqualTo(32);
        assertThat(heatControllerInfo.getSetTemperature()).isEqualTo(53);
        assertThat(heatControllerInfo.getCurrentTemperature()).isEqualTo(56);
        assertThat(heatControllerInfo.isBatteryLow()).isFalse();
        assertThat(heatControllerInfo.getBatteryLevel()).isEqualTo(100);
        assertThat(heatControllerInfo.getErrorCode()).isEqualTo(0);
    }

    @Test
    public void readModelMixedDevices() throws Exception {
        List<DeviceInfo> deviceInfoList = readInput(false, XML_INPUT_THREE_MIXED_DEVICES);

        int countSwitches = 0;
        int countHeatControllers = 0;
        for (DeviceInfo deviceInfo: deviceInfoList) {
            if (deviceInfo.getDeviceType().equals(SwitchDeviceInfo.DEVICE_TYPE)) {
                countSwitches++;
            }
            else if (deviceInfo.getDeviceType().equals(HeatControllerDeviceInfo.DEVICE_TYPE)) {
                countHeatControllers++;
            }
        }

        assertThat(countSwitches).isEqualTo(2);
        assertThat(countHeatControllers).isEqualTo(1);
    }
}
