/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.CameraInfo;
import net.vrfun.homiecenter.model.CameraInfoRepository;
import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * A REST service providing access to IP cameras.
 *
 * @author          boto
 * Creation Date    14th June 2018
 */
@RestController
public class RestServiceCameraDevice {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final CameraInfoRepository cameraInfoRepository;

    private final CameraProxyRoutes cameraProxyRoutes;

    private final AccessUtils accessUtils;


    @Autowired
    public RestServiceCameraDevice(@NonNull final CameraInfoRepository cameraInfoRepository,
                                   @NonNull final CameraProxyRoutes cameraProxyRoutes,
                                   @NonNull final AccessUtils accessUtils) {

        this.cameraInfoRepository = cameraInfoRepository;
        this.cameraProxyRoutes = cameraProxyRoutes;
        this.accessUtils = accessUtils;
    }

    @GetMapping("/api/camera")
    public ResponseEntity<List<CameraInfo>> getCameras(Authentication authentication) {
        List<CameraInfo> cameras = new ArrayList<>();
        cameraInfoRepository.findAll().forEach(camera -> {
            updateCameraUrlTags(camera);
            // we transmit the actual URLs only to admins!
            if (!accessUtils.requestingUserIsAdmin(authentication)) {
                blankCameraURLs(camera);
            }
            cameras.add(camera);
        });
        return new ResponseEntity<>(cameras, HttpStatus.OK);
    }

    private void updateCameraUrlTags(@NonNull CameraInfo camera) {
        try {
            //validate the url
            new URL(camera.getUrl());
            String urlTag = CameraProxyRoutes.getProxyPath() + cameraProxyRoutes.createRouteTag(camera.getUrl()) + "/";
            camera.setUrlTag(urlTag);
        }
        catch (MalformedURLException e) {
            camera.setUrlTag("");
        }

        try {
            new URL(camera.getPreviewUrl());
            String urlTag = CameraProxyRoutes.getProxyPath() + cameraProxyRoutes.createRouteTag(camera.getPreviewUrl()) + "/";
            camera.setPreviewUrlTag(urlTag);
        }
        catch (MalformedURLException e) {
            camera.setPreviewUrlTag("");
        }
    }

    private void blankCameraURLs(@NonNull CameraInfo camera) {
        camera.setUrl("");
        camera.setPreviewUrl("");
    }

    /**
     * Access is restricted to admin user.
     */
    @PostMapping("/api/camera/create")
    public ResponseEntity<CameraInfo> createCamera(@RequestBody CameraInfo reqCreateCamera, Authentication authentication) {
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (StringUtils.isNullOrEmpty(reqCreateCamera.getUrl())) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        createOrUpdateCameraInRepository(reqCreateCamera);

        return new ResponseEntity<>(reqCreateCamera, HttpStatus.OK);
    }

    /**
     * Access is restricted to admin user.
     */
    @PutMapping("/api/camera/update")
    public ResponseEntity<CameraInfo> updateCamera(@RequestBody CameraInfo reqCreateCamera, Authentication authentication) {
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(reqCreateCamera.getId());
        if (!existingCamera.isPresent()) {
            LOGGER.debug("cannot update camera, id {} does not exist!", reqCreateCamera.getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        createOrUpdateCameraInRepository(reqCreateCamera);

        return new ResponseEntity<>(reqCreateCamera, HttpStatus.OK);
    }

    private void createOrUpdateCameraInRepository(@NonNull CameraInfo reqCreateCamera) {
        String url = reqCreateCamera.getUrl();
        if (!StringUtils.isNullOrEmpty(url) && !url.startsWith("http://") &&
                !url.startsWith("https://")) {
            reqCreateCamera.setUrl("http://" + url);
        }
        String previewUrl = reqCreateCamera.getPreviewUrl();
        if (!StringUtils.isNullOrEmpty(previewUrl) && !previewUrl.startsWith("http://") &&
                !previewUrl.startsWith("https://")) {
            reqCreateCamera.setPreviewUrl("http://" + previewUrl);
        }

        updateCameraUrlTags(reqCreateCamera);

        cameraInfoRepository.save(reqCreateCamera);

        // whenever the camera URLs were changed we have to re-build the camera routes and their tags
        cameraProxyRoutes.buildRoutes();
    }

    /**
     * Access is restricted to admin user.
     */
    @PostMapping("/api/camera/delete/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable Long id, Authentication authentication) {
        if (!accessUtils.requestingUserIsAdmin(authentication)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(id);
        if (!existingCamera.isPresent()) {
            LOGGER.debug("cannot delete camera, id {} does not exist!", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        cameraInfoRepository.delete(existingCamera.get());

        // whenever the camera URLs were changed we have to re-build the camera routes and their tags
        cameraProxyRoutes.buildRoutes();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
