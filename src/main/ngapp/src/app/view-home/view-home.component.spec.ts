import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {ViewHomeComponent} from './view-home.component';
import {MaterialModule} from "../material.module";
import {HttpClientTestingModule} from "../../../node_modules/@angular/common/http/testing";
import {FormsModule} from "@angular/forms";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";
import {ApiUserService} from "../service/api-user.service";
import {ViewSwitchDevicesComponent} from "../view-switch-devices/view-switch-devices-component";
import {ViewCameraComponent} from "../view-camera/view-camera.component";
import {SafePipe} from "../safe-pipe";
import {ViewDevicesComponent} from "../view-devices/view-devices.component";
import {ViewHeatControllerDevicesComponent} from "../view-heat-controller-devices/view-heat-controller-devices.component";
import {RouterTestingModule} from "@angular/router/testing";

describe('ViewHomeComponent', () => {
  let component: ViewHomeComponent;
  let fixture: ComponentFixture<ViewHomeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViewHomeComponent,
        ViewDevicesComponent,
        ViewSwitchDevicesComponent,
        ViewHeatControllerDevicesComponent,
        ViewCameraComponent,
        SafePipe
      ],
      imports: [
        MaterialModule,
        FormsModule,
        RouterTestingModule,
        HttpClientTestingModule
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
    fixture = TestBed.createComponent(ViewHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
