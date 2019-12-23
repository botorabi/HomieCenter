/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Map;


/**
 * Utilities for requesting FRITZ!Box via http or https.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
public class Requests {

    /**
     * Request using GET method.
     */
    public ResponseEntity<String> get(@NotNull final String url, @Nullable final Map<String, String> parameters) throws Exception {
        String finalUrl = url;
        if (parameters != null) {
            String params = "";
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (!params.isEmpty()) {
                    params += "&";
                }
                params += entry.getKey() + "=" + entry.getValue();
            }
            if (!params.isEmpty()) {
                finalUrl += "?" + params;
            }
        }

        if (url.startsWith("https")) {
            return createRestTemplateWithoutCertificateValidation().getForEntity(finalUrl, String.class);
        }

        return createRestTemplate().getForEntity(finalUrl, String.class);
    }

    /**
     * Request using POST method.
     */
    public ResponseEntity<String> post(@NotNull final String url, @Nullable final Map<String, String> parameters) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> postData = new LinkedMultiValueMap<>();
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                postData.add(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(postData, headers);

        if (url.startsWith("https")) {
            return createRestTemplateWithoutCertificateValidation().postForEntity(url, request, String.class);
        }

        return createRestTemplate().postForEntity(url, request, String.class);
    }

    /**
     * Create a REST template which supports UTF-8 character set in TEXT response handling.
     */
    @NotNull
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        addUTF8Support(restTemplate);

        return restTemplate;
    }

    /**
     * Create a RestTemplate like createRestTemplate(), but with a proper https validation mechanism.
     * As the certificate of the FRITZ!Box cannot be validated, it is simply ignored during HTTPS requests.
     */
    @NotNull
    protected RestTemplate createRestTemplateWithoutCertificateValidation() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final TrustStrategy TRUSTING_STRATEGY = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, TRUSTING_STRATEGY)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        addUTF8Support(restTemplate);

        return restTemplate;
    }

    private void addUTF8Support(RestTemplate restTemplate) {
        restTemplate.getMessageConverters().stream().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter)converter).setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
    }
}
