/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;


import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Web security configuration
 *
 * @author          boto
 * Creation Date    23th April 2018
*/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    /**
     * Use the environment variable 'homiecenter_disable_security=<true|false>' to enable/disable
     * the spring http access security mechanisms.
     *
     * During development with Angular and using the Node.js web server you may want to disable the
     * security mechanisms, otherwise you run into authentication issues.
     */
    @Value("${homiecenter_disable_security:false}")
    private boolean disableSecurity = false;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (disableSecurity) {
            LOGGER.warn("The application is running without access security mechanisms!");
            configureForNoAccessSecurity(http);
        }
        else {
            configureForFullAccessSecurity(http);
        }
    }

    private void configureForFullAccessSecurity(HttpSecurity http) throws Exception {
        http
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/*", "/api/user/status", "/api/user/login", "/api/user/logout").permitAll()
                .anyRequest().authenticated();
    }

    private void configureForNoAccessSecurity(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http.httpBasic().and()
                .csrf().disable()
                .authorizeExchange()
                //.pathMatchers("/**").authenticated()
                .anyExchange().permitAll()
                .and()
                .build();
    }
}
