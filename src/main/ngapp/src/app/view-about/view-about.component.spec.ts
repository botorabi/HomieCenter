import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAboutComponent } from './view-about.component';
import {MaterialModule} from "../material.module";
import {HttpClientModule} from "../../../node_modules/@angular/common/http";
import {RouterTestingModule} from "../../../node_modules/@angular/router/testing";
import {FormsModule} from "@angular/forms";
import {ApiUserService} from "../service/api-user.service";
import {AppInformationService} from "../service/app-information.service";

describe('ViewAboutComponent', () => {
  let component: ViewAboutComponent;
  let fixture: ComponentFixture<ViewAboutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewAboutComponent ],
      imports: [
        MaterialModule,
        HttpClientModule
      ],
      providers: [
        AppInformationService,
        ApiUserService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewAboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
