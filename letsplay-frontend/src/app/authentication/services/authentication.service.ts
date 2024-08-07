import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, catchError, tap, throwError } from 'rxjs';

import { User } from 'src/app/authentication/models/user.model'
import { profileToUpdate } from '../../core/models/profileToUpdate.model';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }),
  withCredentials: true
};

const AUTH_API = `${environment.apiUrl}/users`;

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public userLoggedIn = this.loggedIn.asObservable();
  private currentUser: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);

  constructor(private http: HttpClient) { }

  login(name: string, password: string): Observable<User> {
    return this.http.post<User>(AUTH_API + '/login', { name, password }, httpOptions).pipe(
      tap((user: User) => {
        this.loggedIn.next(true);
        this.currentUser.next(user);
      })
    );
  }

  refreshToken(token: string) {
    const body = { 'token': token };
    console.log('Refreshing token with body:', body);
    return this.http.post(AUTH_API + `/refreshtoken`, body, httpOptions).pipe(
      tap((response) => console.log('Refresh token response:', response)),
      catchError((error) => {
        console.error('Error refreshing token:', error);
        return throwError(() => error);
      })
    );
  }
  
  logout(): Observable<any> {
    return this.http.post(AUTH_API + '/logout', httpOptions).pipe(
      tap(() => {
        this.loggedIn.next(false);
        this.currentUser.next(null);
      }),
      catchError((error) => {
        console.error('Error logging out:', error);
        localStorage.clear();
        window.location.reload();
        return throwError(() => error);
      })
    );
  }

  register(email: String, name: String, profilePicture: String, password: String){
    return this.http.post<User>(AUTH_API + '/register', {email, name, profilePicture, password}, httpOptions);
  }

  verifyAccount(token: String): Observable<any>{
    const url = `${AUTH_API}/verify/${token}`;
    return this.http.post(url, httpOptions);
  }

  sendResetPasswordEmail(email: String): Observable<any>{
    return this.http.post(AUTH_API + '/resetpassword', {email}, httpOptions);
  }

  resetPassword(token: String, password: String): Observable<any>{
    return this.http.post(AUTH_API + `/resetpassword/${token}`, {password}, httpOptions);
  }

  updatePassword(id: String, password: String): Observable<User> {
    return this.http.put<User>(AUTH_API + '/password' + `/${id}`, password, httpOptions)
  }

  updateUser(id: String, request: profileToUpdate): Observable<User> {
    return this.http.put<User>(AUTH_API + `/${id}`, request, httpOptions).pipe(
      tap((user: User) => {
        this.currentUser.next(user);
      })
    );
  }
  
  getUserById(id: String): Observable<User> {
    return this.http.get<User>(`${AUTH_API}/${id}`, httpOptions);
  }

  deleteUser(id: String): Observable<any> {
    return this.http.delete(AUTH_API + `/${id}`, httpOptions).pipe(
      tap(() => {
        this.loggedIn.next(false);
        this.currentUser.next(null);
      })
    );
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getCurrentUser(): Observable<User | null> {
    return this.currentUser.asObservable();
  }
}
