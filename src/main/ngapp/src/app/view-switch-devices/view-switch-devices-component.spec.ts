import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewSwitchDevicesComponent} from './view-switch-devices-component';
import {MaterialModule} from "../material.module";
import {FormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "../../../node_modules/@angular/common/http/testing";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";
import {ApiUserService} from "../service/api-user.service";

describe('ViewSwitchDevicesComponent', () => {
  let component: ViewSwitchDevicesComponent;
  let fixture: ComponentFixture<ViewSwitchDevicesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewSwitchDevicesComponent ],
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
    fixture = TestBed.createComponent(ViewSwitchDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
