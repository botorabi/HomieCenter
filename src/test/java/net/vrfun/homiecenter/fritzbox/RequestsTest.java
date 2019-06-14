/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class RequestsTest {

    @Mock
    RestTemplate restTemplate;

    private Requests requests;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        requests = spy(new Requests());
    }

    @Test
    public void postNoParameters() throws Exception {
        final String url = "http://test.com";

        mockRestTemplateForPost(url, 0);

        when(requests.createRestTemplate()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.post(url, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private void mockRestTemplateForPost(@Nullable final String url, int countParameters) {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenAnswer((invocation -> {
            String requestUrl = invocation.getArgument(0);
            HttpEntity<MultiValueMap<String, String>> requestBody = invocation.getArgument(1);
            Class responseClass = invocation.getArgument(2);

            HttpStatus status = HttpStatus.OK;
            if ((countParameters != requestBody.getBody().size()) ||
                !url.equals(requestUrl) || (responseClass != String.class)) {
                status = HttpStatus.NOT_ACCEPTABLE;
            }

            ResponseEntity<String> response = new ResponseEntity<>(status);
            return response;
        }));
    }

    @Test
    public void postWithParameters() throws Exception {
        final String url = "http://test.com";
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("param", "paramValue");

        mockRestTemplateForPost(url, parameters.size());

        when(requests.createRestTemplate()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.post(url, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUsingHttps() throws Exception {
        final String url = "https://test.com";
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("param", "paramValue");

        mockRestTemplateForPost(url, parameters.size());

        when(requests.createRestTemplateWithoutCertificateValidation()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.post(url, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getNoParameters() throws Exception {
        final String url = "http://test.com";

        mockRestTemplateForGet(url, 0);

        when(requests.createRestTemplate()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.get(url, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private void mockRestTemplateForGet(@Nullable final String url, int countParameters) {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenAnswer((invocation -> {
            String requestUrl = invocation.getArgument(0);
            Class responseClass = invocation.getArgument(1);

            HttpStatus status = HttpStatus.OK;
            if ((countParameters != StringUtils.countOccurrencesOf(requestUrl, "=")) ||
                    !requestUrl.contains(url) || (responseClass != String.class)) {
                status = HttpStatus.NOT_ACCEPTABLE;
            }

            ResponseEntity<String> response = new ResponseEntity<>(status);
            return response;
        }));
    }

    @Test
    public void getWithParameters() throws Exception {
        final String url = "http://test.com";
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("param1", "paramValue1");
        parameters.put("param2", "paramValue2");

        mockRestTemplateForGet(url, parameters.size());

        when(requests.createRestTemplate()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.get(url, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUsingHttps() throws Exception {
        final String url = "https://test.com";
        final Map<String, String> parameters = new HashMap<>();

        parameters.put("param", "paramValue");

        mockRestTemplateForGet(url, parameters.size());

        when(requests.createRestTemplateWithoutCertificateValidation()).thenReturn(restTemplate);

        ResponseEntity<String> response = requests.get(url, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}