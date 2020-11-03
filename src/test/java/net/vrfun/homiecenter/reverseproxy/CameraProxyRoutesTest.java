/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.reverseproxy;

import net.vrfun.homiecenter.model.CameraInfo;
import net.vrfun.homiecenter.model.CameraInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class CameraProxyRoutesTest {

    public static final String VALID_CAM_URL = "http://mycam.com";
    public static final String VALID_CAM_PREVIEW_URL = "https://mycam.com/preview.jpg";

    private CameraProxyRoutes cameraProxyRoutes;

    @Mock
    private CameraInfoRepository cameraInfoRepository;

    @Mock
    private RefreshableRoutesLocator refreshableRoutesLocator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        cameraProxyRoutes = new CameraProxyRoutes(cameraInfoRepository, refreshableRoutesLocator);
    }

    @NonNull
    private List<CameraInfo> createCamerasWithInvalidURLs() {
        List<CameraInfo> cameras = new ArrayList<>();
        CameraInfo camera = createCamera("Camera_with_invalid_URLs", "h:/bla.com", "p.d@?-#");
        cameras.add(camera);

        Mockito.when(cameraInfoRepository.findAll()).thenReturn(cameras);

        return cameras;
    }

    @NonNull
    private List<CameraInfo> createCamerasWithValidURLs() {
        List<CameraInfo> cameras = new ArrayList<>();
        CameraInfo camera = createCamera("Camera_with_valid_URLs", VALID_CAM_URL, VALID_CAM_PREVIEW_URL);
        cameras.add(camera);

        Mockito.when(cameraInfoRepository.findAll()).thenReturn(cameras);

        return cameras;
    }

    private CameraInfo createCamera(@NonNull final String name,
                                    @NonNull final String url,
                                    @NonNull final String previewUrl) {

        CameraInfo camera = new CameraInfo();
        camera.setId((long)name.hashCode());
        camera.setName(name);
        camera.setUrl(url);
        camera.setPreviewUrl(previewUrl);
        return camera;
    }

    @Test
    public void buildInvalidRoutes() {
        createCamerasWithInvalidURLs();

        cameraProxyRoutes.buildRoutes();

        assertThat(cameraProxyRoutes.getRoutes()).isEmpty();
    }

    @Test
    public void buildValidRoutes() {
        createCamerasWithValidURLs();

        cameraProxyRoutes.buildRoutes();

        assertThat(cameraProxyRoutes.getRoutes().size()).isEqualTo(2);
    }

    @Test
    public void findValidRoutesTags() {
        createCamerasWithValidURLs();

        cameraProxyRoutes.buildRoutes();

        assertThat(cameraProxyRoutes.getRouteTag(VALID_CAM_URL)).isNotNull();
        assertThat(cameraProxyRoutes.getRouteTag(VALID_CAM_PREVIEW_URL)).isNotNull();
    }

    @Test
    public void findValidRoutesUrls() {
        createCamerasWithValidURLs();

        cameraProxyRoutes.buildRoutes();

        final String tagUrl = cameraProxyRoutes.getRouteTag(VALID_CAM_URL);
        final String tagPreviewUrl = cameraProxyRoutes.getRouteTag(VALID_CAM_PREVIEW_URL);

        assertThat(cameraProxyRoutes.getRouteUrl(tagUrl)).isNotNull();
        assertThat(cameraProxyRoutes.getRouteUrl(tagPreviewUrl)).isNotNull();
    }
}