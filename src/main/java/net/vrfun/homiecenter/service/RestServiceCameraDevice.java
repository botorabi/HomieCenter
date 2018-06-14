/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private CameraInfoRepository cameraInfoRepository;

    @GetMapping("/cameradevice")
    public ResponseEntity<List<CameraInfo>> getCameras() {
        List<CameraInfo> cameras = new ArrayList<>();
        cameraInfoRepository.findAll().forEach(camera -> cameras.add(camera));
        return new ResponseEntity<>(cameras, HttpStatus.OK);
    }

    @PostMapping("/cameradevice/createOrUpdate")
    public ResponseEntity<CameraInfo> createOrUpdateCamera(@RequestBody CameraInfo reqCreateCamera) {
        if (reqCreateCamera.getId() != null) {
            Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(reqCreateCamera.getId());
            if (!existingCamera.isPresent()) {
                LOGGER.debug("cannot update camera, id {} does not exist!", reqCreateCamera.getId());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        else {
            if (reqCreateCamera == null || reqCreateCamera.getUrl().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }

        String url = reqCreateCamera.getUrl();
        if (!url.startsWith("http://") &&
                !url.startsWith("https://")) {
            reqCreateCamera.setUrl("http://" + url);
        }
        String previewUrl = reqCreateCamera.getPreviewUrl();
        if (previewUrl != null && !previewUrl.startsWith("http://") &&
                !previewUrl.startsWith("https://")) {
            reqCreateCamera.setPreviewUrl("http://" + previewUrl);
        }

        cameraInfoRepository.save(reqCreateCamera);
        return new ResponseEntity<>(reqCreateCamera, HttpStatus.OK);
    }

    @PostMapping("/cameradevice/delete/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable Long id) {
        Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(id);
        if (!existingCamera.isPresent()) {
            LOGGER.debug("cannot delete camera, id {} does not exist!", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        cameraInfoRepository.delete(existingCamera.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
