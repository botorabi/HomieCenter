/*
 * Copyright (c) 2018 - 2019 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.service;

import net.vrfun.homiecenter.ApplicationProperties;
import net.vrfun.homiecenter.model.*;
import net.vrfun.homiecenter.service.comm.*;
import net.vrfun.homiecenter.testutils.UserTestUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class RestServiceUserTest {

    private RestServiceUser restServiceUser;

    private UserTestUtils userTestUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationProperties applicationProperties;

    private AccessUtils accessUtils;

    private final String EXPECTED_APP_VERSION = "X.Y.Z";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        userTestUtils = new UserTestUtils(userRepository);
        accessUtils = new AccessUtils(userRepository);
        restServiceUser = new RestServiceUser(
                userRepository,
                passwordEncoder,
                accessUtils,
                applicationProperties);

        when(applicationProperties.getAppVersion()).thenReturn(EXPECTED_APP_VERSION);
    }

    @Test
    public void createUser() {
        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setUserName("mylogin");
        reqUserEdit.setPassword("mypassword");
        ResponseEntity<HomieCenterUser> response = restServiceUser.create(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createUserNonAdmin() {
        ReqUserEdit reqUserEdit = new ReqUserEdit();
        ResponseEntity<HomieCenterUser> response = restServiceUser.create(reqUserEdit,
                userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void createUserNonLowerCaseLogin() {
        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setUserName("UpperCaseLogin1234");
        reqUserEdit.setPassword("mypassword");
        ResponseEntity<HomieCenterUser> response = restServiceUser.create(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void createUserTooShortLogin() {
        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setUserName("nam");
        reqUserEdit.setPassword("mypassword");
        ResponseEntity<HomieCenterUser> response = restServiceUser.create(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void createUserWithRepoSaveException() {
        when(userRepository.save(any())).thenThrow(new RuntimeException(""));

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        ResponseEntity<HomieCenterUser> response = restServiceUser.create(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void editUser() {
        userTestUtils.mockUserRepositoryWithUser(false,42L,"some-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(42L);
        reqUserEdit.setPassword("newpassword");

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("some-user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void editUserNonExisting() {
        userTestUtils.mockUserRepositoryWithUser(false,42L,"some-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(40L);

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("some-user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void editUserNotAllowed() {
        userTestUtils.mockUserRepositoryWithUser(false,42L,"some-user", "password");
        userTestUtils.mockUserRepositoryWithUser(false,40L,"some-other-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(40L);

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("another-user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void editUserWithException() {
        userTestUtils.mockUserRepositoryWithUser(true,42L,"admin-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(42L);

        when(userRepository.save(any())).thenThrow(new RuntimeException(""));

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void editUserChangeAdminFlag() {
        userTestUtils.mockUserRepositoryWithUser(true,42L,"admin-user", "password");
        HomieCenterUser user = userTestUtils.mockUserRepositoryWithUser(true,40L,"some-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(40L);
        // try to change the admin flag
        reqUserEdit.setAdmin(false);

        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAdmin()).isFalse();
    }

    @Test
    public void editUserChangeOwnAdminFlag() {
        HomieCenterUser user = userTestUtils.mockUserRepositoryWithUser(true,42L,"admin-user", "password");

        ReqUserEdit reqUserEdit = new ReqUserEdit();
        reqUserEdit.setId(42L);
        // user tries to degrade himself/herself
        reqUserEdit.setAdmin(false);

        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<HomieCenterUser> response = restServiceUser.edit(reqUserEdit,
                userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAdmin()).isTrue();
    }

    @Test
    public void getAllUsersAdmin() {
        int numUsers = prepareUserRepositoryForGetMethods();

        Flux<HomieCenterUser> response = restServiceUser.getAll(userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.count().block()).isEqualTo(numUsers);
    }

    @Test
    public void getAllUsersNonAdmin() {
        prepareUserRepositoryForGetMethods();

        Flux<HomieCenterUser> response = restServiceUser.getAll(userTestUtils.createAuthentication("user", false));

        assertThat(response.count().block()).isEqualTo(1);
        assertThat(response.blockFirst().getUserName()).isEqualTo("user");
    }

    private int prepareUserRepositoryForGetMethods() {
        List<HomieCenterUser> allUsers = Arrays.asList(
                userTestUtils.mockUserRepositoryWithUser(true, 42L, "admin-user", "password"),
                userTestUtils.mockUserRepositoryWithUser(false, 43L, "user", "password"),
                userTestUtils.mockUserRepositoryWithUser(false, 44L, "another-user", "password")
        );

        when(userRepository.findAll()).thenReturn(allUsers);

        return allUsers.size();
    }

    @Test
    public void getByIdNotFound() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<HomieCenterUser> response = restServiceUser.getById(60L, userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getByIdAdmin() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<HomieCenterUser> response = restServiceUser.getById(44L, userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(44L);
    }

    @Test
    public void getByIdNonAdmin() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<HomieCenterUser> response = restServiceUser.getById(42L, userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteById() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<Long> response = restServiceUser.deleteById(43L, userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void deleteByIdNonAdmin() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<Long> response = restServiceUser.deleteById(42L, userTestUtils.createAuthentication("user", false));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteByIdNotFound() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<Long> response = restServiceUser.deleteById(60L, userTestUtils.createAuthentication("user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteByIdSelfDeletion() {
        prepareUserRepositoryForGetMethods();

        ResponseEntity<Long> response = restServiceUser.deleteById(42L, userTestUtils.createAuthentication("admin-user", true));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void getStatusAdmin() {
        Mono<RespUserStatus> response = restServiceUser.getStatus(
                Mono.just(userTestUtils.createAuthentication("admin-user", true)));

        RespUserStatus status = response.block();

        assertThat(status.getAppVersion()).isEqualTo(EXPECTED_APP_VERSION);
        assertThat(status.getRole()).isEqualTo("ADMIN");
        assertThat(status.getName()).isEqualTo("admin-user");
    }

    @Test
    public void getStatusNonAdmin() {
        Mono<RespUserStatus> response = restServiceUser.getStatus(
                Mono.just(userTestUtils.createAuthentication("user", false)));


        RespUserStatus status = response.block();

        assertThat(status.getAppVersion()).isEqualTo(EXPECTED_APP_VERSION);
        assertThat(status.getRole()).isEqualTo("USER");
        assertThat(status.isAuthenticated()).isEqualTo(false);
        assertThat(status.getName()).isEqualTo("user");
    }
}