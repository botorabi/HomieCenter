/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class WebSecurityConfigTest {

    private WebSecurityConfig webSecurityConfig;

    @Mock
    private ReactiveUserDetailsService userDetailsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        webSecurityConfig = new WebSecurityConfig();
    }

    @Test
    public void passwordEncoder() {
        assertThat(webSecurityConfig.createPasswordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    public void resourceRouter() {
        assertThat(webSecurityConfig.staticResourceRouter()).isNotNull();
    }


    @Test
    public void webFilterChain() {
        ServerHttpSecurity serverHttpSecurity = ServerHttpSecurity.http();
        serverHttpSecurity.authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService));

        assertThat(webSecurityConfig.securityWebFilterChain(serverHttpSecurity)).isNotNull();
    }
}