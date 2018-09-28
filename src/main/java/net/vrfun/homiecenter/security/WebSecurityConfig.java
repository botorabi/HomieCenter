/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;


import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.socket.server.*;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * Web security configuration
 *
 * @author          boto
 * Creation Date    23th April 2018
*/
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    /**
     * Is development mode enabled?
     */
    @Value("${enable-dev-mode: false}")
    private boolean developmentModeEnabled;

    /**
     * Pass -Duse-filesystem-resources=true on java command line in order to use filesystem web resources.
     */
    @Value("${use-filesystem-resources: false}")
    private boolean useFileSystemResources;


    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Reactive web seems to need the static path explicitly.
     * In addition we provide a filesystem resource folder during development, it eases the work with Angular.
     */
    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter(){
        if (developmentModeEnabled && useFileSystemResources) {
            File file = new File("");
            String resourceFolder = file.getAbsolutePath() + "/src/main/resources/static/";
            LOGGER.info("Dev run: using filesystem resource folder: {}", resourceFolder);
            return RouterFunctions.resources("/**", new FileSystemResource(resourceFolder));
        }

        return RouterFunctions.resources("/*", new ClassPathResource("static/"));
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                //.csrf().csrfTokenRepository(new WebSessionServerCsrfTokenRepository()).and()
                .csrf().disable()
                .headers()
                    .frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN).and()
                .and()
                .authorizeExchange()
                    .pathMatchers(CameraProxyRoutes.getProxyPath() + "**").authenticated()
                    .pathMatchers("/*", "/assets/**", "/login", "/nav/login", "/nav/about", "/api/user/status").permitAll()
                    .anyExchange().authenticated()
                .and()
                .formLogin()
                    .loginPage("/nav/login")
                    .requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"))
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessHandler((exchange, authentication) -> {
                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.OK);
                        return Mono.empty();
                    })
                .and()
                .build();
    }

    @Bean
    public WebSocketService webSocketService() {
        RequestUpgradeStrategy requestUpgradeStrategy = new ReactorNettyRequestUpgradeStrategy();
        //! NOTE this needs the newer spring-framework version!
        //requestUpgradeStrategy.setMaxFramePayloadLength(2 * 1024 * 1024);
        return new HandshakeWebSocketService(requestUpgradeStrategy);
    }
}
