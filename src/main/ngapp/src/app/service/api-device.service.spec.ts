import { TestBed, inject } from '@angular/core/testing';

import { ApiDeviceService } from './api-device.service';

describe('ApiDeviceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiDeviceService]
    });
  });

  it('should be created', inject([ApiDeviceService], (service: ApiDeviceService) => {
    expect(service).toBeTruthy();
  }));
});
