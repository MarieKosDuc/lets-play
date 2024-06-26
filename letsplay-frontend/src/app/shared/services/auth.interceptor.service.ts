import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS, HttpErrorResponse } from '@angular/common/http';

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
    private eventBusService: EventBusService
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
        // if (
        //   error instanceof HttpErrorResponse &&
        //   error.status === 403
        // ) {
        //   return this.handle403ErrorAndLogoutIfNecessary(req, next);
        // }
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
        console.log('User logged in. Refreshing token for: ' + this.authStorageService.getUser());

        return this.authService.refreshToken(this.refreshToken || '').pipe(
          switchMap(() => {
            this.isRefreshing = false;
            console.log("Authservice in action")
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

  private handle403Error(request: HttpRequest<any>, next: HttpHandler) { // TO DO : redirection vers page 403 avec possibilité de reconnexion
    console.log('Handling 403 error')
    if (this.authStorageService.isLoggedIn()) {
      this.eventBusService.emit(new EventData('logout', null));
      console.log('Logout event emitted')
    }

    return next.handle(request);
  }

  // TODO : redirection vers page 404
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
];