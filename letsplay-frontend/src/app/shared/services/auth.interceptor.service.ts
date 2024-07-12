import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthStorageService } from './storage.service';
import { AuthenticationService } from '../../authentication/services/authentication.service';

import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';

import { EventBusService } from './event-bus.service';
import { EventData } from '../models/event.class';
import { User } from 'src/app/authentication/models/user.model';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshToken?: string;

  constructor(
    private authStorageService: AuthStorageService,
    private authService: AuthenticationService,
    private eventBusService: EventBusService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    req = req.clone({
      withCredentials: true,
    });

    return next.handle(req).pipe(
      catchError((error) => {
        console.log('Error received: ' + error.message + ' with status: ' + error.status)
        if (
          error instanceof HttpErrorResponse &&
          error.status === 401
        ) {
          return this.handle401Error(req, next);
        } 
        if (
          error instanceof HttpErrorResponse &&
          error.status === 403
        ) {
          return this.handle403Error(req, next);
        }
        if (
          error instanceof HttpErrorResponse &&
          error.status === 404 && !req.headers.has('X-Skip-404')
        ) {
          return this.handle404Error(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    console.log('Handling 401 error')
    if (!this.isRefreshing) {
      this.isRefreshing = true;

      if (this.authStorageService.isLoggedIn()) {
        this.refreshToken = this.authStorageService.getUser().refreshToken;
        console.log('Refreshing token for: ' + this.authStorageService.getUser());

        return this.authService.refreshToken(this.refreshToken || '').pipe(
          switchMap(() => {
            this.isRefreshing = false;
            return next.handle(request);
          }),
          catchError((error) => {
            this.isRefreshing = false;
            console.log("Erreur reçue de l'authservice : " + error.message)

            if (error instanceof HttpErrorResponse && error.status === 403) {
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

  private handle403Error(request: HttpRequest<any>, next: HttpHandler) {
    console.log('Handling 403 error')
    if (this.authStorageService.isLoggedIn()) {
      this.eventBusService.emit(new EventData('logout', null));
      console.log('Logout event emitted')
    }

    return next.handle(request);
  }

  private handle404Error(request: HttpRequest<any>, next: HttpHandler) {
    console.log('Handling 404 error');
    this.router.navigate(['/not-found']);
    return next.handle(request);
  }
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];