/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.fritzbox.FRITZBox;
import net.vrfun.homiecenter.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * A REST service providing access to home automation devices connected to a FRITZ!Box.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
@RestController
public class Device {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FRITZBox fritzBox;

    @Autowired
    private CameraInfoRepository cameraInfoRepository;

    @GetMapping("/device")
    public ResponseEntity<List<DeviceInfo>> getDevices() {
        try {
            return new ResponseEntity<>(fritzBox.getDevices(), HttpStatus.OK);
        }
        catch (Exception exception) {
            LOGGER.debug("Problem occurred while retrieving the device list, reason: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/device/{id}")
    public ResponseEntity<DeviceInfo> getDevice(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(fritzBox.getDevice(id), HttpStatus.OK);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/device/{id}/{command}")
    public ResponseEntity<Void> executeCommand(@PathVariable Long id, @PathVariable String command) {
        try {
            fritzBox.handleDeviceCommand(id, command);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception exception) {
            LOGGER.debug("Problem occurred while executing command, reason: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/device/cameraCreateOrUpdate")
    public ResponseEntity<CameraInfo> createOrUpdateCamera(@RequestBody CameraInfo reqCreateCamera) throws Exception {
        if (!fritzBox.getAuthStatus().isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (reqCreateCamera.getId() != null) {
            Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(reqCreateCamera.getId());
            if (!existingCamera.isPresent()) {
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

    @PostMapping("/device/cameraDelete/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable Long id) throws Exception {
        if (!fritzBox.getAuthStatus().isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<CameraInfo> existingCamera = cameraInfoRepository.findById(id);
        if (!existingCamera.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        cameraInfoRepository.delete(existingCamera.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/device/camera")
    public ResponseEntity<List<CameraInfo>> getCameras() throws Exception {
        if (!fritzBox.getAuthStatus().isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<CameraInfo> cameras = new ArrayList<>();
        cameraInfoRepository.findAll().forEach(camera -> cameras.add(camera));
        return new ResponseEntity<>(cameras, HttpStatus.OK);
    }
}
