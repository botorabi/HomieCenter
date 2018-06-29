package net.vrfun.homiecenter.security;

import net.vrfun.homiecenter.fritzbox.FRITZBox;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import reactor.core.publisher.Mono;


public class FritzBoxAuthenticationManager implements ReactiveAuthenticationManager {

    private final Logger LOGGER = LoggerFactory.getLogger(FritzBoxAuthenticationManager.class);

    @Autowired
    private FRITZBox fritzBox;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            boolean loggedIn = fritzBox.getCachedAuthStatus().isAuthenticated();
            LOGGER.debug("*** auth: {}", loggedIn);
            return Mono.just(createAuthentication(loggedIn));
        }
        catch (Exception e) {
            LOGGER.warn("could not determine FRITZ!Box authentication state!");
        }
        return Mono.just(createAuthentication(false));
    }

    private Authentication createAuthentication(boolean loggedIn) {
        Authentication authentication;
        if (loggedIn) {
            authentication = new UsernamePasswordAuthenticationToken(
                    "FBOX_USER",
                    "FBOX_USER_PW",
                    AuthorityUtils.createAuthorityList("ROLE_USER"));
        }
        else {
            authentication = new AnonymousAuthenticationToken(
                    "key",
                    "anonymousUser",
                    AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            authentication.setAuthenticated(false);
        }
        return authentication;
    }
}
