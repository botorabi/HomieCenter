import {TestBed} from '@angular/core/testing';

import {ApiDeviceService} from './api-device.service';
import {Device} from "./device";
import {HttpClientTestingModule} from "../../../node_modules/@angular/common/http/testing";
import {HttpClientMock} from "./test-utils.spec";

describe('ApiDeviceService', () => {

  let apiDeviceService: ApiDeviceService;
  let httpClientMock: HttpClientMock;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiDeviceService],
      imports: [HttpClientTestingModule]
    });

    apiDeviceService = TestBed.get(ApiDeviceService);
    httpClientMock = new HttpClientMock();
  });


  it('should be created', () => {
    expect(apiDeviceService).toBeTruthy();
  });

  it('should fail to retrieve device list', (done) => {

    apiDeviceService.deviceList((devices: Device[], status: any) => {
      expect(devices).toBeNull();
      done();
    });

    httpClientMock
      .mockRequestError('/api/switchdevice', 'ERROR')
      .finalize();
  });

  it('should retrieve a device in list', (done) => {

    apiDeviceService.deviceList((devices: Device[], status: any) => {
      expect(devices.length).toBe(1);
      expect(devices[0].name).toBe("My Device");
      done();
    });

    httpClientMock
      .mockRequest('/api/switchdevice', [{'name': 'My Device', 'id': 'Device ID'}])
      .finalize();
  });
});
