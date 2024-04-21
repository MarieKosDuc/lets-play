import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, tap } from 'rxjs';

import { User } from 'src/app/authentication/models/user.model'


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
    const body = { 'token': token }
    console.log("Pending request to: " + AUTH_API + `/refreshtoken`);
    console.log("Sent body: " + JSON.stringify(body));
    return this.http.post(AUTH_API + `/refreshtoken`, body, httpOptions);
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

  updateUser(password: String, id: String): Observable<User> {
    return this.http.put<User>(AUTH_API + '/update' + `/${id}`, { password }, httpOptions).pipe(
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
