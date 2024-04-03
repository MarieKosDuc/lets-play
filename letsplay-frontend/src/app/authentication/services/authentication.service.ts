import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, tap } from 'rxjs';

import { User } from 'src/app/authentication/models/user.model'


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private currentUser: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);

  constructor(private http: HttpClient) { }
  
  baseUrl: String = `${environment.apiUrl}/users`;


  login(username: string, password: string): Observable<User> {
    const url = this.baseUrl + `/login`;
    return this.http.post<User>(url, { username, password }, httpOptions).pipe(
      tap((user: User) => {
        this.loggedIn.next(true);
        this.currentUser.next(user);
      })
    );
  }

  logout(): Observable<any> {
    const url = `${this.baseUrl}/logout`;
    return this.http.post(url, httpOptions).pipe(
      tap(() => {
        this.loggedIn.next(false);
        this.currentUser.next(null);
      })
    );
  }

  register(email: String, username: String, profilePicture: String, password: String){
    const url = this.baseUrl + '/register'
    return this.http.post<User>(url, {email, username, profilePicture, password}, httpOptions);
  }

  updateUser(password: String, id: String): Observable<User> {
    const url = this.baseUrl + '/update' + `/${id}`;
    return this.http.put<User>(url, { password }, httpOptions).pipe(
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
