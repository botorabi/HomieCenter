import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ViewAuthenticationComponent} from './view-authentication.component';
import {MaterialModule} from "../material.module";
import {AppInformationService} from "../service/app-information.service";
import {ApiUserService} from "../service/api-user.service";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FormsModule} from "@angular/forms";

describe('ViewAuthenticationComponent', () => {
  let component: ViewAuthenticationComponent;
  let fixture: ComponentFixture<ViewAuthenticationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewAuthenticationComponent ],
      imports: [
        MaterialModule,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule
      ],
      providers: [
        AppInformationService,
        ApiUserService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewAuthenticationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
