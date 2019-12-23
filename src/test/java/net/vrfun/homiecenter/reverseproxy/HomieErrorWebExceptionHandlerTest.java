/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class HomieErrorWebExceptionHandlerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private ResourceProperties resourceProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Mock
    private ServerWebExchange serverWebExchange;

    private HomieErrorWebExceptionHandler homieErrorWebExceptionHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        homieErrorWebExceptionHandler = new HomieErrorWebExceptionHandler(
                errorAttributes,
                resourceProperties,
                applicationContext);
    }

    @Test
    public void getRoutingFunction() {
        homieErrorWebExceptionHandler.getRoutingFunction(errorAttributes);
    }

    @Test
    public void afterPropertiesSet() {
        homieErrorWebExceptionHandler.afterPropertiesSet();
    }

    @Test
    public void handleCameraError() {
        ServerHttpRequest serverHttpRequest = mockRequestResponse();

        final String cameraRoutePath = CameraProxyRoutes.getProxyPath() + "XYZ";
        RequestPath requestPath = RequestPath.parse(URI.create(cameraRoutePath + "/index.html"), cameraRoutePath);
        doReturn(requestPath).when(serverHttpRequest).getPath();

        homieErrorWebExceptionHandler.handle(serverWebExchange, new Throwable("Test Throwable"));
    }

    private ServerHttpRequest mockRequestResponse() {
        ServerHttpRequest serverHttpRequest = Mockito.mock(ServerHttpRequest.class);
        ServerHttpResponse serverHttpResponse = Mockito.mock(ServerHttpResponse.class);
        HttpHeaders headers = mock(HttpHeaders.class);

        doReturn(new DefaultDataBufferFactory()).when(serverHttpResponse).bufferFactory();
        doReturn(serverHttpRequest).when(serverWebExchange).getRequest();
        doReturn(serverHttpResponse).when(serverWebExchange).getResponse();

        doReturn(headers).when(serverHttpResponse).getHeaders();

        return serverHttpRequest;
    }

    @Test
    public void handleNonCameraError() {
        ServerHttpRequest serverHttpRequest = mockRequestResponse();

        RequestPath requestPath = RequestPath.parse(URI.create("/anypath/index.html"), "/anypath");
        doReturn(requestPath).when(serverHttpRequest).getPath();

        homieErrorWebExceptionHandler.handle(serverWebExchange, new Throwable("Test Throwable"));
    }

    @Test
    public void createPage() {
        assertThat(homieErrorWebExceptionHandler.createMissingCameraPage()).isNotEmpty();
    }

    @Test
    public void getResourceImage() {
        assertThat(homieErrorWebExceptionHandler.getResourceImage()).isNotEmpty();
    }
}