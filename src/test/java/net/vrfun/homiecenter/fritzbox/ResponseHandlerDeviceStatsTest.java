/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.DeviceStats;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ResponseHandlerDeviceStatsTest {

    private ResponseHandlerDeviceStats responseHandlerDeviceStats;

    protected static final String XML_INPUT_WRONG_CONTENT =
            "<nondevicestats></nondevicestats>";

    protected static final String XML_INPUT_BAD_VALUES =
            "<devicestats>\n" +
                    "   <temperature>\n" +
                    "       <stats count=\"3\" grid=\"900\">\n" +
                    "           300,-,BADVALUE\n" +
                    "       </stats>\n" +
                    "   </temperature>\n" +
                    "</devicestats>\n";

    protected static final String XML_INPUT =
            "<devicestats>\n" +
            "   <temperature>\n" +
            "       <stats count=\"96\" grid=\"900\">\n" +
            "           300,300,300,295,295,305,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-\n" +
            "       </stats>\n" +
            "   </temperature>\n" +
            "   <voltage>\n" +
            "       <stats count=\"360\" grid=\"10\">\n" +
            "           229533,229533,229533,229533,229533,229533,229533,229533,229533,229533,229533,229533,229184,229184,229184,229184,229184,229184,229184,229184,229184,229184,229184,229184,232058,232058,232058,232058,232058,232058,232058,232058,232058,232058,232058,232058,231661,231661,231661,231661,231661,231661,231661,231661,231661,231661,231661,231661,231787,231787,231787,231787,231787,231787,231787,231787,231787,231787,231787,231787,231471,231471,231471,231471,231471,231471,231471,231471,231471,231471,231471,231471,230934,230934,230934,230934,230934,230934,230934,230934,230934,230934,230934,230934,231221,231221,231221,231221,231221,231221,231221,231221,231221,231221,231221,231221,231205,231205,231205,231205,231205,231205,231205,231205,231205,231205,231205,231205,231430,231430,231430,231430,231430,231430,231430,231430,231430,231430,231430,231430,230849,230849,230849,230849,230849,230849,230849,230849,230849,230849,230849,230849,231193,231193,231193,231193,231193,231193,231193,231193,231193,231193,231193,231193,231350,231350,231350,231350,231350,231350,231350,231350,231350,231350,231350,231350,230677,230677,230677,230677,230677,230677,230677,230677,230677,230677,230677,230677,230922,230922,230922,230922,230922,230922,230922,230922,230922,230922,230922,230922,229901,229901,229901,229901,229901,229901,229901,229901,229901,229901,229901,229901,230862,230862,230862,230862,230862,230862,230862,230862,230862,230862,230862,230862,230862,230772,230742,231104,230404,231109,230065,230479,231049,230815,231281,230699,230867,231290,231230,230987,230951,230891,230823,230184,230608,230595,231459,231272,231163,231285,231320,230737,231496,231339,231645,230429,231125,231125,231125,231125,231125,230910,230910,230910,230910,230910,230910,230910,230910,230910,230910,230910,230910,230910,230111,230096,230204,231216,230890,230470,230594,230561,229792,230480,230084,230379,230379,230190,230206,229765,229241,229526,230072,230620,230799,230799,230799,230799,230799,230206,230206,230206,230206,230206,230206,230206,230206,230206,230206,230206,230206,230634,230634,230634,230634,230634,230634,230634,230634,230634,230634,230634,230634,230634,230667,230480,230834,230558,230382,230903,231097,230431,230016,229032,230333,229754,230936,229903,230877,230960,230733,230099,230400,230717,231045,229936,230315,230618,230877,230870,230294,229774,229992,230273,230440,230818,230291,230291,230291,230291,230291,230291,230291,230291,230291,230291,230291,230011,230011,230011,230011,230011,230011,230011,230011,230011,230011,230011,230011,230540\n" +
            "       </stats>\n" +
            "   </voltage>\n" +
            "   <power>\n" +
            "       <stats count=\"360\" grid=\"10\">\n" +
            "           472,472,472,472,472,472,472,472,472,472,472,472,464,464,464,464,464,464,464,464,464,464,464,464,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,479,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,464,464,464,464,464,464,464,464,464,464,464,464,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,464,464,464,464,464,464,464,464,464,464,464,464,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,457,479,472,464,457,457,464,464,507,457,457,457,464,472,472,464,472,472,472,457,472,479,464,500,457,464,464,464,457,486,457,457,464,464,464,464,464,522,522,522,522,522,522,522,522,522,522,522,522,522,472,486,457,479,464,479,464,493,457,464,464,493,493,486,472,457,457,486,464,479,464,464,464,464,464,472,472,472,472,472,472,472,472,472,472,472,472,486,486,486,486,486,486,486,486,486,486,486,486,486,464,472,479,500,464,479,464,457,457,479,457,486,464,479,450,479,464,472,457,479,457,479,464,472,472,479,457,479,457,500,450,479,457,457,457,457,457,457,457,457,457,457,457,479,479,479,479,479,479,479,479,479,479,479,479,457\n" +
            "       </stats>\n" +
            "   </power>\n" +
            "   <energy>\n" +
            "       <stats count=\"12\" grid=\"2678400\">120,0,0,0,0,0,0,0,0,0,0,0</stats>\n" +
            "       <stats count=\"31\" grid=\"86400\">68,45,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0</stats>\n" +
            "   </energy>\n" +
            "   <unknownfield>\n" +
            "   </unknownfield>\n" +
            "</devicestats>\n";

    @Before
    public void setup() {
        responseHandlerDeviceStats = new ResponseHandlerDeviceStats();
    }

    private Document createDocument(final String content) {
        try {
            return responseHandlerDeviceStats.parseResponse(content);
        } catch (Throwable throwable) {
        }
        return null;
    }

    @Test
    public void invalidFormat() {
        assertThat(createDocument("")).isNull();
        assertThat(createDocument("<invalid-tag>")).isNull();
    }


    @Test
    public void invalidContent() throws Exception {
        DeviceStats deviceStats = readInput(false, XML_INPUT_WRONG_CONTENT);

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getEnergy().getStats().size()).isZero();
        assertThat(deviceStats.getTemperature().getStats().size()).isZero();
        assertThat(deviceStats.getPower().getStats().size()).isZero();
    }

    @Test
    public void badValues() throws Exception {
        DeviceStats deviceStats = readInput(false, XML_INPUT_BAD_VALUES);

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getTemperature().getStats().size()).isZero();
    }

    @Test
    public void readEnergy() throws Exception {
        DeviceStats deviceStats = readInput(false, XML_INPUT);

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getEnergy().getStats().size()).isEqualTo(2);
        assertThat(deviceStats.getEnergy().getStats().get(0).getValues().size()).isEqualTo(12);
        assertThat(deviceStats.getEnergy().getStats().get(0).getGrid()).isEqualTo(2678400);
        assertThat(deviceStats.getEnergy().getStats().get(1).getValues().size()).isEqualTo(31);
        assertThat(deviceStats.getEnergy().getStats().get(1).getGrid()).isEqualTo(86400);
    }

    @Test
    public void readTemperature() throws Exception {
        DeviceStats deviceStats = readInput(false, XML_INPUT);

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getTemperature().getStats()).isNotEmpty();
        assertThat(deviceStats.getTemperature().getStats().get(0).getValues().size()).isNotZero();
        assertThat(deviceStats.getTemperature().getStats().get(0).getGrid()).isEqualTo(900);
    }

    @Nullable
    private DeviceStats readInput(final boolean caseSensitive, @NotNull final String inputString) throws Exception {
        DeviceStats deviceStats = new DeviceStats();
        responseHandlerDeviceStats.setUseCaseSensitiveNames(caseSensitive);
        try {
            responseHandlerDeviceStats.read(inputString, deviceStats);
        } catch (Exception exception) {
            return null;
        }
        return deviceStats;
    }

    @Test
    public void readPower() throws Exception {
        DeviceStats deviceStats = readInput(false, XML_INPUT);

        assertThat(deviceStats).isNotNull();
        assertThat(deviceStats.getPower().getStats()).isNotEmpty();
        assertThat(deviceStats.getPower().getStats().get(0).getValues().size()).isNotZero();
        assertThat(deviceStats.getPower().getStats().get(0).getGrid()).isEqualTo(10);
    }
}