import { TestBed, inject } from '@angular/core/testing';

import { AppInformationService } from './app-information.service';
import {HttpClientTestingModule} from "../../../node_modules/@angular/common/http/testing";
import {ApiUserService} from "./api-user.service";
import {UserStatus} from "./user-status";

describe('AppInformationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AppInformationService, ApiUserService],
      imports: [HttpClientTestingModule]
    });
  });

  it('should be created', inject([AppInformationService], (service: AppInformationService) => {
    expect(service).toBeTruthy();
  }));

  it('user should not be authenticated', () => {
    let appInformationService = new AppInformationService(null);

    appInformationService.setUserStatus(null);

    expect(appInformationService.isUserAuthenticated()).toBeFalsy();

    let userStatus = new UserStatus();
    userStatus.authenticated = false;
    appInformationService.setUserStatus(userStatus);

    expect(appInformationService.isUserAuthenticated()).toBeFalsy();
  });

  it('user should be authenticated', () => {
    let appInformationService = new AppInformationService(null);

    let userStatus = new UserStatus();
    userStatus.authenticated = true;
    appInformationService.setUserStatus(userStatus);

    expect(appInformationService.isUserAuthenticated()).toBeTruthy();
  });
});
