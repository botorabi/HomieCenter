/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.net.*;

/**
 * A gateway route resolver which is used for dynamically refresh routes during the application runtime.
 *
 * @author          boto
 * Creation Date    25th June 2018
 */
@Component
public class RefreshableRoutesLocator implements RouteLocator {

    private RouteLocatorBuilder builder;
    private RouteLocatorBuilder.Builder routesBuilder;
    private Flux<Route> route;

    @Autowired
    GatewayRoutesRefresher gatewayRoutesRefresher;

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    public RefreshableRoutesLocator(RouteLocatorBuilder builder) {
        this.builder = builder;
        clearRoutes();
    }

    /**
     * Remove all routes.
     */
    public void clearRoutes() {
        routesBuilder = builder.routes();
    }

    /**
     * Add a new route. After adding all routes call 'buildRoutes'.
     */
    @NotNull
    public RefreshableRoutesLocator addRoute(@NotNull final String id, @NotNull final String path, @NotNull final URI uri) throws URISyntaxException {
        if (StringUtils.isNullOrEmpty(uri.getScheme())) {
            throw new URISyntaxException("Missing scheme in URI: {}", uri.toString());
        }
        routesBuilder.route(id, r -> r
                .path(path + "/**")
                .filters(f -> {
                    f.addRequestHeader("X-Forwarded-Prefix", path);
                    f.stripPrefix(1);
                    // setup the retry filter, it is important as during transitions from one page to another access problems can occur.
                    f.retry(config -> {
                        config.setRetries(5);
                        config.setStatuses(HttpStatus.INTERNAL_SERVER_ERROR);
                        config.setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE);
                    });
                    return f;
                })
                .uri(uri)
        );
        return this;
    }

    /**
     * Call this method in order to publish all routes defined by 'addRoute' calls.
     */
    public void buildRoutes() {
        this.route = routesBuilder.build().getRoutes();
        gatewayRoutesRefresher.refreshRoutes();
    }

    @Override
    public Flux<Route> getRoutes() {
        return route;
    }
}
