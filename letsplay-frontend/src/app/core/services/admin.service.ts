import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, tap } from 'rxjs';

import { Ad } from '../models/ad.model';
import { AdCreation } from '../models/adCreation.model';
import { User } from '../../authentication/models/user.model';

import { AuthStorageService } from 'src/app/shared/services/storage.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient, private authStorageService: AuthStorageService) { }

  public users: User[] = [];

  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/admin/users`, httpOptions).pipe(
      tap((users: User[]) => {
        this.users = users;
      })
    );
  }

  public deleteUser(id: string): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/admin/users/${id}`, httpOptions);
  }

  public deleteAd(id: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/admin/ads/${id}`, httpOptions);
  }
}
