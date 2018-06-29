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
import org.springframework.security.core.userdetails.*;
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

    /**
     * Reactive web needs the static path explicitly.
     */
    @Bean
    RouterFunction<ServerResponse> staticResourceRouter(){
        return RouterFunctions.resources("/*", new ClassPathResource("static/"));
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                //.csrf().csrfTokenRepository(new WebSessionServerCsrfTokenRepository()).and()
                .csrf().disable()
                .headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN).and()
                .and()
                .authorizeExchange()
                .pathMatchers(CameraProxyRoutes.getProxyPath() + "**").authenticated()
                .pathMatchers("/api/user/status").permitAll()
                .anyExchange().authenticated()
                .and().formLogin()
                .and().build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build();

        UserDetails user2 = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(user1, user2);
    }

/*
    @Autowired
    private ContextRepository contextRepository;

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                //.csrf().csrfTokenRepository(new WebSessionServerCsrfTokenRepository()).and()
                .csrf().disable()
                .headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN).and()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new ExceptionAuthEntryPoint())
                .and()
//                .securityContextRepository(contextRepository)
                .authenticationManager(reactiveAuthenticationManager())
                .authorizeExchange()
                .pathMatchers(CameraProxyRoutes.getProxyPath() + "**").authenticated()
                .pathMatchers("/*", "/api/user/status", "/api/user/login", "/api/user/logout").permitAll()
                .anyExchange().authenticated()
                .and()
                .build();
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(){
        return new FritzBoxAuthenticationManager();
    }
*/


/*

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http
			.authorizeExchange()
				.pathMatchers(HttpMethod.GET, "/posts/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")
				//.pathMatchers("/users/{user}/**").access(this::currentUserMatchesPath)
				.anyExchange().authenticated()
				.and()
			.build();
	}

	private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication, AuthorizationContext context) {
		return authentication
			.map( a -> context.getVariables().get("user").equals(a.getName()))
			.map( granted -> new AuthorizationDecision(granted));
	}

	@Bean
	public MapReactiveUserDetailsService userDetailsRepository() {
		UserDetails rob = User.withDefaultPasswordEncoder().username("test").password("password").roles("USER").build();
		UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("password").roles("USER","ADMIN").build();
		return new MapReactiveUserDetailsService(rob, admin);
	}
 */
}
