import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS, HttpErrorResponse } from '@angular/common/http';

import { StorageService } from '../_services/storage.service';
import { AuthenticationService } from '../authentication/services/authentication.service';

import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

import { EventBusService } from '../_shared/event-bus.service';
import { EventData } from '../_shared/event.class';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshToken?: string;

  constructor(
    private storageService: StorageService,
    private authService: AuthenticationService,
    private eventBusService: EventBusService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
      withCredentials: true,
    });

    return next.handle(req).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401
        ) {
          return this.handle401Error(req, next);
        }

        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;

      if (this.storageService.isLoggedIn()) {
        this.refreshToken = this.storageService.getUser().refreshToken;


        return this.authService.refreshToken(this.refreshToken || '').pipe(
          switchMap(() => {
            this.isRefreshing = false;
            console.log('Refreshed token')

            return next.handle(request);
          }),
          catchError((error) => {
            this.isRefreshing = false;
            console.log('HTTP error', error);

            if (error.status == '403') {
              this.eventBusService.emit(new EventData('logout', null));
              console.log('Logout event emitted')
            }

            return throwError(() => error);
          })
        );
      }
    }

    return next.handle(request);
  }
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];