import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewUserComponent} from './view-user.component';
import {MaterialModule} from "../material.module";
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {HttpClientMock} from "../service/test-utils.spec";
import {User} from "../service/user";

describe('ViewUserComponent', () => {
  let component: ViewUserComponent;
  let fixture: ComponentFixture<ViewUserComponent>;
  let httpClientMock: HttpClientMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewUserComponent ],
      imports: [
        MaterialModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        ApiUserService,
        AppInformationService
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpClientMock = new HttpClientMock();
  }));

  it('should create', () => {
    httpClientMock.mockRequest('/api/user', [new User()]);

    expect(component).toBeTruthy();
  });
});
