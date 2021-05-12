/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import net.vrfun.homiecenter.utils.StaticResourceLoader;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Customized exception handler for unreachable services (IP Cameras) behind the proxy.
 *
 * @author          boto
 * Creation Date    5th September 2018
 */

@Component
public class HomieErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String ERROR_TEXT = "Camera Not Available!";

    private static final String IMAGE_FILE = "static/assets/link_off.svg";

    private static String errorResponse;

    @Bean
    @Order(-1)
    public HomieErrorWebExceptionHandler createHomieErrorWebExceptionHandler(
            @NonNull final ErrorAttributes errorAttributes,
            @NonNull final ResourceProperties resourceProperties,
            @NonNull final ApplicationContext applicationContext) {

        return new HomieErrorWebExceptionHandler(errorAttributes, resourceProperties, applicationContext);
    }

    public HomieErrorWebExceptionHandler(@NonNull final ErrorAttributes errorAttributes,
                                         @NonNull final ResourceProperties resourceProperties,
                                         @NonNull final ApplicationContext applicationContext) {

        super(errorAttributes, resourceProperties, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return null;
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        final ServerHttpResponse response = exchange.getResponse();
        final String path = exchange.getRequest().getPath().value();

        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().setContentType(MediaType.TEXT_HTML);

        byte[] content = path.startsWith(CameraProxyRoutes.getProxyPath()) ?
                createMissingCameraPage() : createMissingResource(path);

        DataBuffer dataBuffer = response.bufferFactory().allocateBuffer().write(content);

        return response.writeWith(Mono.just(dataBuffer));
    }

    protected byte[] createMissingResource(@NonNull final String url) {
        String content =
                "<!doctype html>\n<html lang=\"en\">\n<head>\n</head>\n" +
                        "<body>\n" + getMissingResourceHint(url) + "</body>\n</html>";

        return content.getBytes(StandardCharsets.UTF_8);
    }

    protected String getMissingResourceHint(@NonNull final String resourceUrl) {
        String hint = "<p>Requested resource with URL <i>" + resourceUrl + "</i> does not exist!</p>";
        hint += "<p><a href='/'>Return to main page.</a></p>";
        return hint;
    }

    protected byte[] createMissingCameraPage() {
        String content =
                "<!doctype html>\n<html lang=\"en\">\n<head>\n</head>\n" +
                        "<body>\n" + getResourceImage() + "</body>\n</html>";

        return content.getBytes(StandardCharsets.UTF_8);
    }

    protected String getResourceImage() {
        if (errorResponse != null) {
            return errorResponse;
        }

        StaticResourceLoader staticResourceLoader = new StaticResourceLoader();
        final byte[] image = staticResourceLoader.getBinaryResource(IMAGE_FILE);

        errorResponse = (image != null) ? createImageElement(image) : ERROR_TEXT;

        return errorResponse;
    }

    protected String createImageElement(@NonNull final byte[] data) {
        String response =
                "<div style='margin: auto; width: 70%; text-align: center;'>\n" +
                "  <img style='width: 100%;' src='data:image/svg+xml;utf8,";
        response += new String(data, StandardCharsets.UTF_8);
        response += "' alt='" + ERROR_TEXT + "' />\n";
        response += "<p>IP Camera is not available!</p>";
        response += "</div>";

        return response;
    }
}
