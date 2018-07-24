/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.security;

import net.vrfun.homiecenter.model.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsService userDetailsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userDetailsService = new UserDetailsService(userRepository);
    }

    @Test
    public void emptyUserRepository() {
        createEmptyUserRepository();
        ReactiveUserDetailsService service = userDetailsService.createUserDetailsService();
        UserDetails userDetails = service.findByUsername("Any User").block();

        assertThat(userDetails).isNull();
    }

    @Test
    public void userRepositoryWithAdmin() {
        createUserRepositoryWithUser(true, "Administrator", "Password");
        ReactiveUserDetailsService service = userDetailsService.createUserDetailsService();
        UserDetails userDetails = service.findByUsername("Administrator").block();

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("Administrator");
        assertThat(userDetails.getPassword()).isEqualTo("Password");
    }

    private void createEmptyUserRepository() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
    }

    private void createUserRepositoryWithUser(boolean admin, @NotNull final String userName, @NotNull final String password) {
        HomieCenterUser adminUser = new HomieCenterUser();
        adminUser.setAdmin(admin);
        adminUser.setRealName(userName);
        adminUser.setUserName(userName);
        adminUser.setPassword(password);
        Optional<HomieCenterUser> foundUser = Optional.of(adminUser);
        when(userRepository.findByUserName(any())).thenReturn(foundUser);
    }
}