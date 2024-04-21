import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, tap } from 'rxjs';

import { Ad } from '../models/ad.model';
import { AdCreation } from '../models/adCreation.model';
import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from 'src/app/authentication/models/user.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

const ADS_API = `${environment.apiUrl}/ads`;

@Injectable({
    providedIn: 'root'
})

export class AdService {

  constructor(private http: HttpClient, private authService: AuthenticationService) { }
  
  ads: Ad[] = [];
  ad!: Ad;

  user!: User | null;

  ngOnInit() {
    this.authService.getCurrentUser().subscribe((user: User | null) => {
      this.user = user; //TODO : remplacer par storage après merge
    });
  }

    getAllAds(): Observable<Ad[]> {
      return this.http.get<Ad[]>(ADS_API + `/get/all`, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    getAdById(id: number): Observable<Ad> {
      return this.http.get<Ad>(ADS_API + `/get/${id}`, httpOptions).pipe(
        tap((ad: Ad) => {
          this.ad = ad;
        })
      );
    }

    getUserAds(id: string): Observable<Ad[]> {
      return this.http.get<Ad[]>(ADS_API + `/get/user/${id}`, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    searchAds(musicianType: string, styles: string[], location: string): Observable<Ad[]> {
      const url = ADS_API + `/search?musicianType=${musicianType}&styles=${styles}&location=${location}`;
      return this.http.get<Ad[]>(url, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    createAd(ad: AdCreation) {
      console.log(ad);
      return this.http.post<AdCreation>(ADS_API + `/create`, ad, httpOptions);
    }

}
