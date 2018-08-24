import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewUserEditComponent} from './view-user-edit.component';
import {MaterialModule} from "../material.module";
import {RouterTestingModule} from "@angular/router/testing";
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";
import {FormsModule} from "@angular/forms";
import {HttpClientMock} from "../service/test-utils.spec";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {appRoutes} from "../app.routes";

describe('ViewUserEditComponent', () => {
  let component: ViewUserEditComponent;
  let fixture: ComponentFixture<ViewUserEditComponent>;
  let httpClientMock: HttpClientMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewUserEditComponent ],
      imports: [
        MaterialModule,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule
      ],
      providers: [
        ApiUserService,
        AppInformationService
      ]
    })
    .compileComponents();

    RouterTestingModule.withRoutes(appRoutes);
    httpClientMock = new HttpClientMock();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewUserEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
