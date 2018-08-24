import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewCameraEditComponent} from './view-camera-edit.component';
import {MaterialModule} from "../material.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AppInformationService} from "../service/app-information.service";
import {ApiUserService} from "../service/api-user.service";
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {ApiDeviceService} from "../service/api-device.service";

describe('ViewCameraEditComponent', () => {
  let component: ViewCameraEditComponent;
  let fixture: ComponentFixture<ViewCameraEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewCameraEditComponent ],
      imports: [
        MaterialModule,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule
      ],
      providers: [
        AppInformationService,
        ApiDeviceService,
        ApiUserService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewCameraEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
