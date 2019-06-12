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
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
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

    private final RouteLocatorBuilder builder;
    private final GatewayRoutesRefresher gatewayRoutesRefresher;

    private RouteLocatorBuilder.Builder routesBuilder;
    private Flux<Route> route;

    @Autowired
    public RefreshableRoutesLocator(@NonNull final RouteLocatorBuilder builder,
                                    @NonNull final GatewayRoutesRefresher gatewayRoutesRefresher) {
        this.builder = builder;
        this.gatewayRoutesRefresher = gatewayRoutesRefresher;

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

        routesBuilder.route(id, fn -> fn
                .path(path + "/**")
                .filters(filterSpec -> setupRouteFilters(path, uri, filterSpec))
                .uri(uri)
        );

        return this;
    }

    @NotNull
    private UriSpec setupRouteFilters(@NotNull final String path, @NotNull final URI uri, @NotNull GatewayFilterSpec filterSpec) {
        filterSpec.stripPrefix(1);

        // setup the retry filter, it is important as during transitions from one page to another access problems can occur.
        filterSpec.retry(config -> {
            config.setRetries(5);
            config.setStatuses(HttpStatus.INTERNAL_SERVER_ERROR);
            config.setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE);
        });

        String prefixPath = uri.getPath();
        if (!StringUtils.isNullOrEmpty(prefixPath)) {
            filterSpec.setPath(prefixPath);
        }

        // handle redirects coming from a routed service
        //  the service may be aware of request header 'X-Forwarded-Prefix'
        filterSpec.addRequestHeader("X-Forwarded-Prefix", path);
        //  as a fallback for services not aware of 'X-Forwarded-Prefix' we correct the Location header in response
        filterSpec.filter(new ModifyResponseHeaderLocationGatewayFilterFactory().apply(c -> c.setName(path + "/")));

        return filterSpec;
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
