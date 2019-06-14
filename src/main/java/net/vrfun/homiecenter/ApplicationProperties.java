/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;


@Service
@PropertySource("file:homiecenter.properties")
public class ApplicationProperties {

    @Value("${build.version}")
    private String appVersion;

    @Value("${fritzbox.url:fritz.box}")
    private String fritzBoxUrl;

    @Value("${fritzbox.username:}")
    private String fritzBoxUserName;

    @Value("${fritzbox.password:}")
    private String fritzBoxPassword;

    @Bean
    public ApplicationProperties createApplicationProperties() {
        return new ApplicationProperties();
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getFritzBoxUrl() {
        return fritzBoxUrl;
    }

    public String getFritzBoxUserName() {
        return fritzBoxUserName;
    }

    public String getFritzBoxPassword() {
        return fritzBoxPassword;
    }
}
