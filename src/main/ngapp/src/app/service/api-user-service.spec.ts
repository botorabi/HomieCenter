import {inject, TestBed} from '@angular/core/testing';

import {ApiUserService} from './api-user.service';
import {HttpClientTestingModule} from "../../../node_modules/@angular/common/http/testing";
import {UserStatus} from "./user-status";
import {HttpClientMock} from "./test-utils.spec";


describe('ApiUserService', () => {
  let apiUserService: ApiUserService;
  let httpClientMock: HttpClientMock;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiUserService],
      imports: [HttpClientTestingModule]
    });

    apiUserService = TestBed.get(ApiUserService);
    httpClientMock = new HttpClientMock();
  });

  it('should be created', inject([ApiUserService], (service: ApiUserService) => {
    expect(service).toBeTruthy();
  }));

  it('should fail to login', (done) => {
    apiUserService.login('username', 'password', (userStatus: UserStatus) => {
      expect(userStatus.authenticated).toBeFalsy();
      done();
    });

    let userStatus = new UserStatus();
    userStatus.authenticated = false;

    httpClientMock
      .mockRequest('/login', '')
      .mockUserStatus(userStatus)
      .finalize();
  });

  it('should successfully login', (done) => {
    apiUserService.login('username', 'password', (userStatus: UserStatus) => {
      expect(userStatus.authenticated).toBeTruthy();
      expect(userStatus.role).toBe('ADMIN');
      expect(userStatus.name).toBe('admin');
      done();
    });

    let userStatus = new UserStatus();
    userStatus.authenticated = true;
    userStatus.role = 'ADMIN';
    userStatus.name = 'admin';

    httpClientMock
      .mockRequest('/login', '')
      .mockUserStatus(userStatus)
      .finalize();
  });
});
