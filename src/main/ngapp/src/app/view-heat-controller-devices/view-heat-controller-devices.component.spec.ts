import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewHeatControllerDevicesComponent} from './view-heat-controller-devices.component';
import {MaterialModule} from "../material.module";
import {FormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";
import {ApiUserService} from "../service/api-user.service";

describe('ViewHeatControllerDevicesComponent', () => {
  let component: ViewHeatControllerDevicesComponent;
  let fixture: ComponentFixture<ViewHeatControllerDevicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewHeatControllerDevicesComponent ],
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
    fixture = TestBed.createComponent(ViewHeatControllerDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
