/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import net.vrfun.homiecenter.model.CameraInfoRepository;
import net.vrfun.homiecenter.utils.HashGenerator;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A service dealing with proxy routes used for IP cameras.
 *
 * @author          boto
 * Creation Date    20th June 2018
 */
@Service
@Configuration
public class CameraProxyRoutes {

    private final Logger LOGGER = LoggerFactory.getLogger(CameraProxyRoutes.class);

    private final static String PROXY_PATH = "/camera";

    @Autowired
    private CameraInfoRepository cameraInfoRepository;

    @Autowired
    private RefreshableRoutesLocator refreshableRoutesLocator;

    private Map<String /*Tag*/, String /*Path*/> routes = new ConcurrentHashMap<>();


    @Bean
    public CameraProxyRoutes createProxyRoutes() {
        return new CameraProxyRoutes();
    }

    public CameraProxyRoutes() {
    }

    @NotNull
    public final String getProxyPath() {
        return PROXY_PATH;
    }

    @NotNull
    public final Map<String, String> getRoutes() {
        return routes;
    }

    @NotNull
    public String getRoute(@NotNull final String cameraTag) {
        return routes.getOrDefault(cameraTag, "");
    }

    /**
     * Whenever the routes were changed then call this method in order to publish the changes in the application.
     */
    public void buildRoutes() {
        routes.clear();
        cameraInfoRepository.findAll().forEach(camera -> {
            String previewUrl = camera.getPreviewUrl();
            String url = camera.getUrl();
            if (previewUrl != null && !previewUrl.isEmpty()) {
                routes.put(createRouteTag(previewUrl), previewUrl);
            }
            if (url != null && !url.isEmpty()) {
                routes.put(createRouteTag(url), url);
            }
        });

        updateGatewayRoutes();
    }

    private void updateGatewayRoutes() {
        refreshableRoutesLocator.clearRoutes();
        routes.forEach((tag, uri) -> {
            refreshableRoutesLocator.addRoute("camera" + tag, getProxyPath() /*+ tag*/, uri);
            LOGGER.info("adding proxy path: tag {} -> {}", getProxyPath() + tag, uri);
        });
        refreshableRoutesLocator.buildRoutes();
    }

    @NotNull
    public String createRouteTag(@NotNull final String url) {
        try {
            return HashGenerator.createMD5(url.getBytes());
        }
        catch (Exception ex) {
            LOGGER.error("Could not create route hash for URL {}, reason: {}", url, ex.getMessage());
        }
        return "";
    }
}
