/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class IndexHtmlTest {

    private IndexHtml indexHtml;

    @Before
    public void setup() {
        indexHtml = new IndexHtml();
    }

    @Test
    public void home() {
        ResponseEntity<String> response = indexHtml.home();

        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    public void angularPaths() {
        ResponseEntity<String> response = indexHtml.angularPaths();

        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    public void getIndexHtmlContent() {
        assertThat(indexHtml.getIndexHtmlContent()).isNotEmpty();
    }
}