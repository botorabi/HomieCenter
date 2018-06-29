package net.vrfun.homiecenter.security;

import org.slf4j.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.*;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;


/**
 * Manage the security context.
 *
 * @author          boto
 * Creation Date    27th April 2018
 */
//@Component
public class ContextRepository {//implements ServerSecurityContextRepository {
/*
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ContextRepository createContextRepository() {
        return new ContextRepository();
    }

    public ContextRepository() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        LOGGER.debug("*** save context: {}, auth: {}", context, context == null ? "NONE" : context.getAuthentication());
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        SecurityContext context = ReactiveSecurityContextHolder.getContext().block();
        Authentication auth = getOrCreateAuthentication(context);
        LOGGER.debug("*** load context, user: {}, auth: {}", auth.getPrincipal(), auth.isAuthenticated());

        return Mono.just(context);
    }

    private Authentication getOrCreateAuthentication(SecurityContext context) {
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            authentication = new AnonymousAuthenticationToken(
                    "key",
                    "anonymousUser",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

            authentication.setAuthenticated(false);
            context.setAuthentication(authentication);
        }

        return authentication;
    }
    */
}

