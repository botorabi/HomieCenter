/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.testutils.UserTestUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(SpringRunner.class)
public class AccessUtilsTest {

    private AccessUtils accessUtils;

    @Mock
    private UserRepository userRepository;

    private UserTestUtils userTestUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accessUtils = new AccessUtils(userRepository);
        userTestUtils = new UserTestUtils(userRepository);
    }

    @Test
    public void requestingUserIsAdminOrOwner() {
        userTestUtils.mockUserRepositoryWithUser(true,0L,"admin-user", "pw");
        userTestUtils.mockUserRepositoryWithUser(false,0L,"non-admin-user", "pw");

        boolean notExistingUser = accessUtils.requestingUserIsAdminOrOwner(
                userTestUtils.createAuthentication("non-existing-user", false),
                userTestUtils.createUser("another-non-existing-user"));

        assertThat(notExistingUser).isFalse();


        boolean isAdmin = accessUtils.requestingUserIsAdminOrOwner(
                userTestUtils.createAuthentication("admin-user", true),
                userTestUtils.createUser("admin-user"));

        assertThat(isAdmin).isTrue();


        boolean isAnotherAdmin = accessUtils.requestingUserIsAdminOrOwner(
                userTestUtils.createAuthentication("admin-user", true),
                userTestUtils.createUser("another-admin-user"));

        assertThat(isAnotherAdmin).isTrue();


        boolean isNotAdmin = accessUtils.requestingUserIsAdminOrOwner(
                userTestUtils.createAuthentication("non-admin-user", false),
                userTestUtils.createUser("some-user"));

        assertThat(isNotAdmin).isFalse();


        boolean isNotAdminButOwner = accessUtils.requestingUserIsAdminOrOwner(
                userTestUtils.createAuthentication("non-admin-user", false),
                userTestUtils.createUser("non-admin-user"));

        assertThat(isNotAdminButOwner).isTrue();
    }

    @Test
    public void requestingUserIsOwner() {
        userTestUtils.mockUserRepositoryWithUser(false, 0L,"some-user", "pw");

        boolean userIsOwner = accessUtils.requestingUserIsOwner(
                userTestUtils.createAuthentication("some-user", false),
                userTestUtils.createUser("some-user"));

        assertThat(userIsOwner).isTrue();

        boolean userIsNotOwner = accessUtils.requestingUserIsOwner(
                userTestUtils.createAuthentication("some-user", false),
                userTestUtils.createUser("another-user"));

        assertThat(userIsNotOwner).isFalse();

        boolean userNotExisting = accessUtils.requestingUserIsOwner(
                userTestUtils.createAuthentication("some-user", false),
                Optional.empty());

        assertThat(userNotExisting).isFalse();
    }

    @Test
    public void requestingUserIsOwnerNonExisting() {
        userTestUtils.mockUserRepositoryWithUser(false,0L,"some-user", "pw");

        boolean userIsOwner = accessUtils.requestingUserIsOwner(
                userTestUtils.createAuthentication("not-existing-user", false),
                userTestUtils.createUser("some-user"));

        assertThat(userIsOwner).isFalse();
    }

    @Test
    public void requestingUserIsAdmin() {
        boolean userIsAdmin = accessUtils.requestingUserIsAdmin(userTestUtils.createAuthentication("admin-user", true));

        assertThat(userIsAdmin).isTrue();
    }

    @Test
    public void roleExists() {
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_1"),
                new SimpleGrantedAuthority("ROLE_2")
        );

        assertThat(accessUtils.roleExists(null, "dont-care")).isFalse();
        assertThat(accessUtils.roleExists(authorities, "ROLE_X")).isFalse();
        assertThat(accessUtils.roleExists(authorities, "ROLE_1")).isTrue();
        assertThat(accessUtils.roleExists(authorities, "ROLE_2")).isTrue();
    }
}