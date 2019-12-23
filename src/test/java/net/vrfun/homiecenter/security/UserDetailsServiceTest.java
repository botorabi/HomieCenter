/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;

import net.vrfun.homiecenter.model.UserRepository;
import net.vrfun.homiecenter.testutils.UserTestUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsService userDetailsService;

    private UserTestUtils userTestUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userDetailsService = new UserDetailsService(userRepository);
        userTestUtils = new UserTestUtils(userRepository);
    }

    @Test
    public void emptyUserRepository() {
        userTestUtils.mockEmptyUserRepository();
        ReactiveUserDetailsService service = userDetailsService.createUserDetailsService();
        UserDetails userDetails = service.findByUsername("Any User").block();

        assertThat(userDetails).isNull();
    }

    @Test
    public void userRepositoryWithAdmin() {
        userTestUtils.mockUserRepositoryWithUser(true,0L,"Administrator", "Password");
        ReactiveUserDetailsService service = userDetailsService.createUserDetailsService();
        UserDetails userDetails = service.findByUsername("Administrator").block();

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("Administrator");
        assertThat(userDetails.getPassword()).isEqualTo("Password");
    }
}