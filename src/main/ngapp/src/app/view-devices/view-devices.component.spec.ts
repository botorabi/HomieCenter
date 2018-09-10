import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewDevicesComponent} from './view-devices.component';
import {MaterialModule} from "../material.module";
import {FormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";
import {ApiUserService} from "../service/api-user.service";
import {ViewSwitchDevicesComponent} from "../view-switch-devices/view-switch-devices-component";
import {ViewHeatControllerDevicesComponent} from "../view-heat-controller-devices/view-heat-controller-devices.component";

describe('ViewDevicesComponent', () => {
  let component: ViewDevicesComponent;
  let fixture: ComponentFixture<ViewDevicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViewDevicesComponent,
        ViewSwitchDevicesComponent,
        ViewHeatControllerDevicesComponent
      ],
      imports: [
        MaterialModule,
        FormsModule,
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
    fixture = TestBed.createComponent(ViewDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
