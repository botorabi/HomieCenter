package net.vrfun.homiecenter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;


@Service
@PropertySource("file:homiecenter.properties")
public class ApplicationProperties {

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
