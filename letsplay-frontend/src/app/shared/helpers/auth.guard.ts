import { Injectable, inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn } from '@angular/router';

import { AuthStorageService } from '../services/auth.storage.service';
import { RolesEnum } from '../enums/rolesEnum';

@Injectable({
    providedIn: 'root'
  })
  class PermissionsService {
  
    constructor(private router: Router, private authStorageService: AuthStorageService) {}
  
    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (this.authStorageService.isLoggedIn()) {
            return true;
        }
        return false;
    }

    canActivateAdmin(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (this.authStorageService.isLoggedIn() && this.authStorageService.getRole().includes(RolesEnum.ADMIN)) {
            return true;
        }
        return false;
    }
  }
  
  export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
    return inject(PermissionsService).canActivate(next, state);
  }

  export const AdminAuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
    return inject(PermissionsService).canActivateAdmin(next, state);
  }
