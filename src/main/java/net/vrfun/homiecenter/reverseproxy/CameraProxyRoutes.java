/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import net.vrfun.homiecenter.model.CameraInfoRepository;
import net.vrfun.homiecenter.utils.HashGenerator;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A service dealing with proxy routes used for IP cameras.
 *
 * @author          boto
 * Creation Date    20th June 2018
 */
@Service
public class CameraProxyRoutes {

    private final Logger LOGGER = LoggerFactory.getLogger(CameraProxyRoutes.class);

    private final static String PROXY_PATH = "/camera";

    private final CameraInfoRepository cameraInfoRepository;

    private final RefreshableRoutesLocator refreshableRoutesLocator;

    private Map<String /*Tag*/, String /*Path*/> routes = new ConcurrentHashMap<>();


    @Autowired
    public CameraProxyRoutes(@NonNull final CameraInfoRepository cameraInfoRepository,
                             @NonNull final RefreshableRoutesLocator refreshableRoutesLocator) {

        this.cameraInfoRepository = cameraInfoRepository;
        this.refreshableRoutesLocator = refreshableRoutesLocator;
    }

    @NonNull
    public static final String getProxyPath() {
        return PROXY_PATH;
    }

    @NonNull
    public final Map<String, String> getRoutes() {
        return routes;
    }

    @Nullable
    public String getRouteUrl(@NonNull final String cameraTag) {
        return routes.getOrDefault(cameraTag, null);
    }

    @Nullable
    public String getRouteTag(@NonNull final String cameraUrl) {
        for (Map.Entry<String, String> entry: routes.entrySet()) {
            if (cameraUrl.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Whenever the routes were changed then call this method in order to publish the changes in the application.
     */
    public void buildRoutes() {
        LOGGER.info("building proxy routes");

        routes.clear();

        cameraInfoRepository.findAll().forEach(camera -> {
            String previewUrl = camera.getPreviewUrl();
            String url = camera.getUrl();
            if (isValidUrl(previewUrl)) {
                routes.put(createRouteTag(previewUrl), previewUrl);
            }
            if (isValidUrl(url)) {
                routes.put(createRouteTag(url), url);
            }
        });

        updateGatewayRoutes();
    }

    private boolean isValidUrl(@Nullable String url) {
        if (StringUtils.isNullOrEmpty(url)) {
            return false;
        }

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    private void updateGatewayRoutes() {
        refreshableRoutesLocator.clearRoutes();
        routes.forEach((tag, uri) -> {
            try {
                URI validUri = new URI(uri);
                refreshableRoutesLocator.addRoute("camera" + tag, getProxyPath() + tag, validUri);
                LOGGER.info("adding proxy path: tag {} -> {}", getProxyPath() + tag, uri);
            } catch (URISyntaxException e) {
                LOGGER.info("skip invalid proxy path: {}", uri);
            }
        });
        refreshableRoutesLocator.buildRoutes();
    }

    @NonNull
    public String createRouteTag(@NonNull final String url) {
        try {
            return HashGenerator.createMD5(url.getBytes());
        }
        catch (Exception ex) {
            LOGGER.error("Could not create route hash for URL {}, reason: {}", url, ex.getMessage());
        }
        return "";
    }
}
