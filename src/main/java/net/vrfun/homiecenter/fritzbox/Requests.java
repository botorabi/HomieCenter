/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
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

    private HttpClient httpClient;

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
            return restTemplateWithoutCertificateValidation().getForEntity(finalUrl, String.class);
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
            return restTemplateWithoutCertificateValidation().postForEntity(url, request, String.class);
        }

        return createRestTemplate().postForEntity(url, request, String.class);
    }

    /**
     * Create a RestTemplate with a proper https validation mechanism.
     * As the certificate of the FRITZ!Box cannot be validated, it is simply ignored during HTTPS requests.
     * The given URL must begin with "https://".
     */
    @NotNull
    private RestTemplate restTemplateWithoutCertificateValidation() throws Exception {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClient());
        RestTemplate restTemplate = createRestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    /**
     * Create a REST template which supports UTF-8 character set in TEXT response handling.
     */
    @NotNull
    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().stream().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter)converter).setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        return restTemplate;
    }

    @NotNull
    private HttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        return httpClient;
    }

    @NotNull
    private HttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,  String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } }, new SecureRandom());


        SSLSocketFactory sf = new SSLSocketFactory(sslContext);
        Scheme httpsScheme = new Scheme("https", 443, sf);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);
        ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(cm);

        return httpClient;
    }
}
