/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ResponseHandlerAuthStatusTest {

    private ResponseHandlerAuthStatus responseHandlerAuthStatus;

    private static final String XML_INPUT =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<SessionInfo>" +
                    "  <SID>0000000000000000</SID>" +
                    "  <Challenge>0368b443</Challenge>" +
                    "  <BlockTime>20</BlockTime>" +
                    "  <Rights></Rights>" +
                    "</SessionInfo>";

    private static final String XML_INPUT_LOWER_CASE_SID =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<SessionInfo>" +
                    "  <sID>42</sID>" +
                    "  <Challenge>0368b443</Challenge>" +
                    "  <BlockTime>0</BlockTime>" +
                    "  <Rights></Rights>" +
                    "</SessionInfo>";


    private static final String XML_INPUT_DEFAULTS =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<SessionInfo>" +
                    "</SessionInfo>";

    private static final String XML_INPUT_WIHT_RIGHTS =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<SessionInfo>" +
            "  <SID>10bcc23b290ae67a</SID>" +
            "  <Challenge>b916bb17</Challenge>" +
            "  <BlockTime>0</BlockTime>" +
            "  <Rights>" +
            "    <Name>Dial</Name>" +
            "    <Access>2</Access>" +
            "    <Name>App</Name>" +
            "    <Access>2</Access>" +
            "    <Name>HomeAuto</Name>" +
            "    <Access>2</Access>" +
            "    <Name>BoxAdmin</Name>" +
            "    <Access>2</Access>" +
            "    <Name>Phone</Name>" +
            "    <Access>2</Access>" +
            "    <Name>NAS</Name>" +
            "    <Access>2</Access>" +
            "  </Rights>" +
            "</SessionInfo>";

    @Before
    public void setup() {
        responseHandlerAuthStatus = new ResponseHandlerAuthStatus();
    }

    private Document createDocument(final String content) {
        try {
            return responseHandlerAuthStatus.parseResponse(content);
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
    public void readModelCaseInsensitive() throws Exception {
        AuthStatus authStatus = readInput(false, XML_INPUT);

        assertThat(authStatus.getSID()).isEqualTo("0000000000000000");
        assertThat(authStatus.getChallenge()).isEqualTo("0368b443");
        assertThat(authStatus.getBlockTime()).isEqualTo(20);
        assertThat(authStatus.getRights()).isEmpty();
    }

    @Test
    public void readModelCaseSensitive() throws Exception {
        AuthStatus authStatus = readInput(true, XML_INPUT_LOWER_CASE_SID);

        assertThat(authStatus.getSID()).isNotEqualTo("42");
        assertThat(authStatus.getChallenge()).isEqualTo("0368b443");
        assertThat(authStatus.getBlockTime()).isEqualTo(0);
        assertThat(authStatus.getRights()).isEmpty();
    }

    @Test
    public void readModelUseDefaults() throws Exception {
        AuthStatus authStatus = readInput(false, XML_INPUT_DEFAULTS);

        assertThat(authStatus.getSID()).isEqualTo("0");
        assertThat(authStatus.getChallenge()).isEqualTo("");
        assertThat(authStatus.getBlockTime()).isEqualTo(0);
        assertThat(authStatus.getRights()).isNullOrEmpty();
    }

    @Test
    public void readModelWithRights() throws Exception {
        AuthStatus authStatus = readInput(false, XML_INPUT_WIHT_RIGHTS);

        assertThat(authStatus.getSID()).isEqualTo("10bcc23b290ae67a");
        assertThat(authStatus.getChallenge()).isEqualTo("b916bb17");
        assertThat(authStatus.getBlockTime()).isEqualTo(0);
        assertThat(authStatus.getRights()).isNotEmpty();
        assertThat(authStatus.getRights()).containsKeys("Dial", "App", "HomeAuto", "BoxAdmin", "Phone", "NAS");
        assertThat(authStatus.getRights().get("Dial")).isEqualTo("2");
    }

    private AuthStatus readInput(final boolean caseSensitive, @NotNull final String inputString) throws Exception {
        AuthStatus authStatus = new AuthStatus();
        responseHandlerAuthStatus.setUseCaseSensitiveNames(caseSensitive);
        responseHandlerAuthStatus.read(inputString, authStatus);
        return authStatus;
    }
}
