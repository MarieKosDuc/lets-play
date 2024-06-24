import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from 'src/app/authentication/models/user.model';

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class AuthStorageService {
  public user$: Observable<User>;

  private userSubject: BehaviorSubject<any>;

  private storageEventSubject = new BehaviorSubject<boolean>(false);

  constructor() {
    this.userSubject = new BehaviorSubject<any>(this.getUser());
    this.user$ = this.userSubject.asObservable();

    window.addEventListener('storage', (event) => {
      if (event.storageArea === sessionStorage) {
        this.storageEventSubject.next(true);
        this.userSubject.next(this.getUser()); // Met à jour l'état du user
      }
    });
  }

  clean(): void {
    window.sessionStorage.clear();
    this.userSubject.next(null);
  }

  public saveUser(user: any): void {
    console.log('saveUser', user);
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
    this.userSubject.next(user);
  }

  public getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return {};
  }

  public isLoggedIn(): boolean {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }

  getStorageEventObservable() {
    return this.storageEventSubject.asObservable();
  }

}