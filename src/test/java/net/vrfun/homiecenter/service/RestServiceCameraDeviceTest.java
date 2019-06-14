/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.reverseproxy.CameraProxyRoutes;
import net.vrfun.homiecenter.testutils.UserTestUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class RestServiceCameraDeviceTest {

    private RestServiceCameraDevice restServiceCameraDevice;

    private AccessUtils accessUtils;

    private UserTestUtils userTestUtils;

    @Mock
    private CameraInfoRepository cameraInfoRepository;

    @Mock
    private CameraProxyRoutes cameraProxyRoutes;

    @Mock
    private UserRepository userRepository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        accessUtils = new AccessUtils(userRepository);
        userTestUtils = new UserTestUtils(userRepository);
        restServiceCameraDevice = new RestServiceCameraDevice(cameraInfoRepository, cameraProxyRoutes, accessUtils);
    }

    private void createCamerasInRepository() {
        CameraInfo cameraInfo1 = createCamera("cam1", "http://preview", "http://mainview");
        CameraInfo cameraInfo2 = createCamera("cam2", "http://preview2", "http://mainview2");

        when(cameraInfoRepository.findAll()).thenReturn(Arrays.asList(cameraInfo1, cameraInfo2));
    }

    private void createCamerasInRepositoryWithBadUrls() {
        CameraInfo cameraInfo1 = createCamera("cam1", "ht//preview", "blup://mainview");
        CameraInfo cameraInfo2 = createCamera("cam2", "http-!\\preview2", "mainview2^");

        when(cameraInfoRepository.findAll()).thenReturn(Arrays.asList(cameraInfo1, cameraInfo2));
    }

    @NotNull
    private CameraInfo createCamera(@NotNull final String name, @NotNull final String previewUrl, @NotNull final String url) {
        CameraInfo camera = new CameraInfo();
        camera.setName(name);
        camera.setPreviewUrl(previewUrl);
        camera.setUrl(url);

        return camera;
    }

    @Test
    public void getCamerasEmptyList() {
        ResponseEntity<List<CameraInfo>> response = restServiceCameraDevice.getCameras(
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void getCamerasAsAdmin() {
        createCamerasInRepository();

        ResponseEntity<List<CameraInfo>> response = restServiceCameraDevice.getCameras(
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<CameraInfo> cameras = response.getBody();
        assertThat(cameras).isNotEmpty();
        assertThat(cameras.get(0).getUrl()).isNotEmpty();
        assertThat(cameras.get(0).getPreviewUrl()).isNotEmpty();
        assertThat(cameras.get(0).getUrlTag()).isNotEmpty();
        assertThat(cameras.get(0).getPreviewUrlTag()).isNotEmpty();
    }

    @Test
    public void getCamerasAsAdminWithBadUrls() {
        createCamerasInRepositoryWithBadUrls();

        ResponseEntity<List<CameraInfo>> response = restServiceCameraDevice.getCameras(
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<CameraInfo> cameras = response.getBody();
        assertThat(cameras).isNotEmpty();
        assertThat(cameras.get(0).getUrl()).isNotEmpty();
        assertThat(cameras.get(0).getPreviewUrl()).isNotEmpty();
        // bad URLs result in empty tags
        assertThat(cameras.get(0).getUrlTag()).isEmpty();
        assertThat(cameras.get(0).getPreviewUrlTag()).isEmpty();
    }

    @Test
    public void getCamerasAsNonAdmin() {
        createCamerasInRepository();

        ResponseEntity<List<CameraInfo>> response = restServiceCameraDevice.getCameras(
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<CameraInfo> cameras = response.getBody();
        assertThat(cameras).isNotEmpty();
        // non-admins do not get the actual service URLs
        assertThat(cameras.get(0).getUrl()).isEmpty();
        assertThat(cameras.get(0).getPreviewUrl()).isEmpty();
    }

    @Test
    public void createOrUpdateCameraNonAdminAccess() {
        ResponseEntity<CameraInfo> response = restServiceCameraDevice.createCamera(null,
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        response = restServiceCameraDevice.updateCamera(null,
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void createCameraInvalidCamera() {
        CameraInfo reqCreateCamera = new CameraInfo();
        reqCreateCamera.setUrl("");

        ResponseEntity<CameraInfo> response = restServiceCameraDevice.createCamera(reqCreateCamera,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void createCamera() {
        CameraInfo reqCreateCamera = new CameraInfo();
        reqCreateCamera.setUrl("https://mycam.com");
        reqCreateCamera.setPreviewUrl("https://mycam.com/preview");

        ResponseEntity<CameraInfo> response = restServiceCameraDevice.createCamera(reqCreateCamera,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateCameraNonExistingCamera() {
        CameraInfo reqCreateCamera = new CameraInfo();
        reqCreateCamera.setId(42L);

        ResponseEntity<CameraInfo> response = restServiceCameraDevice.updateCamera(reqCreateCamera,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateCameraExistingCamera() {
        CameraInfo cameraInfo = new CameraInfo();
        cameraInfo.setId(42L);
        cameraInfo.setName("camera");
        cameraInfo.setUrl("mycam.com");
        cameraInfo.setPreviewUrl("mycam.com/preview");

        when(cameraInfoRepository.findById(42L)).thenReturn(Optional.of(cameraInfo));

        CameraInfo reqCreateCamera = new CameraInfo();
        reqCreateCamera.setId(42L);

        ResponseEntity<CameraInfo> response = restServiceCameraDevice.updateCamera(reqCreateCamera,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteCameraNonAdmin() {
        ResponseEntity<Void> response = restServiceCameraDevice.deleteCamera(0L,
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteNonExistingCamera() {
        when(cameraInfoRepository.findById(42L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = restServiceCameraDevice.deleteCamera(42L,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteExistingCamera() {
        CameraInfo cameraInfo = new CameraInfo();
        cameraInfo.setId(42L);

        when(cameraInfoRepository.findById(42L)).thenReturn(Optional.of(cameraInfo));

        ResponseEntity<Void> response = restServiceCameraDevice.deleteCamera(42L,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}