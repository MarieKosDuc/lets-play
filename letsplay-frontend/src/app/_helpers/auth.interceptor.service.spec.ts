import { TestBed } from '@angular/core/testing';

import { HttpRequestInterceptor } from './auth.interceptor.service';

describe('AuthInterceptorService', () => {
  let service: HttpRequestInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HttpRequestInterceptor);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
