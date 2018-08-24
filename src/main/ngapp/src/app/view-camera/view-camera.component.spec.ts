import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewCameraComponent} from './view-camera.component';
import {MaterialModule} from "../material.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {AppInformationService} from "../service/app-information.service";
import {ApiDeviceService} from "../service/api-device.service";
import {ApiUserService} from "../service/api-user.service";
import {SafePipe} from "../safe-pipe";

describe('ViewCameraComponent', () => {
  let component: ViewCameraComponent;
  let fixture: ComponentFixture<ViewCameraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewCameraComponent, SafePipe ],
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
    fixture = TestBed.createComponent(ViewCameraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
