/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * A gateway filter which modifies the LOCATION response header of a routed path.
 *
 * NOTE: Redirects caused by a routed service may need a prefix, this filter cares about that prefix.
 *
 * @author          boto
 * Creation Date    24th July 2018
 */
public class ModifyResponseHeaderLocationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    public ModifyResponseHeaderLocationGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(NAME_KEY);
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            final String locationUri = headers.getFirst(HttpHeaders.LOCATION);
            if (locationUri != null) {
                if (locationUri.isEmpty() || locationUri.equals("/")) {
                    headers.set(HttpHeaders.LOCATION, config.getName());
                }
            }
        }));
    }
}
