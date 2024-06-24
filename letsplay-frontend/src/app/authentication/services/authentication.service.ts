import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, catchError, tap, throwError } from 'rxjs';

import { User } from 'src/app/authentication/models/user.model'
import { profileToUpdate } from '../../shared/models/profileToUpdate.model';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' })
};

const AUTH_API = `${environment.apiUrl}/users`;

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private currentUser: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<User> {
    return this.http.post<User>(AUTH_API + '/login', { username, password }, httpOptions).pipe(
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
      tap((response) => console.log('Refresh token response:', response)), // Débogage : afficher la réponse du serveur
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
      })
    );
  }

  register(email: String, username: String, profilePicture: String, password: String){
    return this.http.post<User>(AUTH_API + '/register', {email, username, profilePicture, password}, httpOptions);
  }

  verifyAccount(token: String): Observable<any>{
    const url = `${AUTH_API}/verify/${token}`;
    return this.http.post(url, httpOptions);
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

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getCurrentUser(): Observable<User | null> {
    return this.currentUser.asObservable();
  }
}
