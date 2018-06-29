/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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
    public RefreshableRoutesLocator addRoute(@NotNull final String id, @NotNull final String path, @NotNull final URI uri) {
        routesBuilder.route(id, r -> r
                .path(path + "/**")
                .filters(f -> {
                    f.addRequestHeader("X-Forwarded-Prefix", path);
                    f.stripPrefix(1);
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
