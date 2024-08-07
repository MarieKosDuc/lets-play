import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from 'src/app/authentication/models/user.model';

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root',
})
export class AuthStorageService {
  public user$: Observable<User>;

  private userSubject: BehaviorSubject<any>;

  private storageEventSubject = new BehaviorSubject<boolean>(false);

  constructor() {
    this.userSubject = new BehaviorSubject<any>(this.getUser());
    this.user$ = this.userSubject.asObservable();

    window.addEventListener('storage', (event) => {
      if (event.storageArea === localStorage) {
        this.storageEventSubject.next(true);
        this.userSubject.next(this.getUser());
      }
    });
  }

  clean(): void {
    window.localStorage.clear();
    this.userSubject.next({});
  }

  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.userSubject.next(user);
  }

  public getUser(): any {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return {};
  }

  public getRole(): string {
    const user = this.getUser();
    return user.roles;
  }

  public isLoggedIn(): boolean {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }


  public toggleFavoriteInStorage(adId: number): void {
    let currentUser = this.getUser();

    if (!currentUser) {
      return;
    }

    const index = currentUser.likedAds.indexOf(adId);

    if (index === -1) {
      currentUser.likedAds.push(adId);
    } else {
      currentUser.likedAds.splice(index, 1);
    }

    this.saveUser(currentUser);
  }

  public getStorageEventObservable() {
    return this.storageEventSubject.asObservable();
  }
}
