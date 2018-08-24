import {async, TestBed} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {MaterialModule} from "./material.module";
import {RouterModule} from "@angular/router";
import {AppInformationService} from "./service/app-information.service";
import {ApiUserService} from "./service/api-user.service";


describe('AppComponent', () => {

  let appInfoService: any;
  let apiUserService: any;
  let router: any;

  class MockRouter {
    url = "";

    setCurrrentRoute(url: string) {
      this.url = url;
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports: [
        MaterialModule,
        RouterModule
      ]
    }).compileComponents();

    apiUserService = new ApiUserService(null);
    appInfoService = new AppInformationService(apiUserService);
    router = new MockRouter();
  }));

  afterEach(() => {
  });

  it('should create the app', async(() => {
    let app = new AppComponent(appInfoService, apiUserService, router);
    expect(app).toBeTruthy();
  }));

  it('should have menu title Edit User', async(() => {
    let app = new AppComponent(appInfoService, apiUserService, router);
    router.setCurrrentRoute("/nav/user-edit");
    expect(app.getMenuTitle()).toEqual('Edit User');
  }));

  it('should have menu title Homie Center', async(() => {
    let app = new AppComponent(appInfoService, apiUserService, router);
    router.setCurrrentRoute("");
    expect(app.getMenuTitle()).toEqual('Homie Center');
  }));
});
