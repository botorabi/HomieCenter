/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;


import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.reactive.function.server.*;

/**
 * Web security configuration
 *
 * @author          boto
 * Creation Date    23th April 2018
*/
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Reactive web needs the static path explicitly.
     */
    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter(){
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
                    .pathMatchers("/api/user/status").permitAll()
                    .anyExchange().authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .and().build();
    }
}
