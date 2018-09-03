import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewCameraDetailsComponent} from './view-camera-details.component';
import {MaterialModule} from "../material.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AppInformationService} from "../service/app-information.service";
import {ApiUserService} from "../service/api-user.service";
import {SafePipe} from "../safe-pipe";

describe('ViewCameraDetailsComponent', () => {
  let component: ViewCameraDetailsComponent;
  let fixture: ComponentFixture<ViewCameraDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewCameraDetailsComponent, SafePipe ],
      imports: [
        MaterialModule,
        HttpClientTestingModule
      ],
      providers: [
        AppInformationService,
        ApiUserService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewCameraDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
