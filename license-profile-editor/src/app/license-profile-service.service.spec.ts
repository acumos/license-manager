import { TestBed } from '@angular/core/testing';

import { LicenseProfileServiceService } from './license-profile-service.service';

describe('LicenseProfileServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LicenseProfileServiceService = TestBed.get(LicenseProfileServiceService);
    expect(service).toBeTruthy();
  });
});
