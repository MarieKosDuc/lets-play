import { TestBed } from '@angular/core/testing';

import { AuthStorageService } from './auth.storage.service';

describe('StorageServiceService', () => {
  let authStorageService: AuthStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    authStorageService = TestBed.inject(AuthStorageService);
  });

  it('should be created', () => {
    expect(authStorageService).toBeTruthy();
  });
});
