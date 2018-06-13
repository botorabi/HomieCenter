import { TestBed, inject } from '@angular/core/testing';

import { AppInformationService } from './app-information.service';

describe('AppInformationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AppInformationService]
    });
  });

  it('should be created', inject([AppInformationService], (service: AppInformationService) => {
    expect(service).toBeTruthy();
  }));
});
