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

const httpOptionsWithSkip404 = {
  headers: httpOptions.headers.set('X-Skip-404', 'true')
  .set('Content-Type', 'application/json'),
  withCredentials: true
};

const ADS_API = `${environment.apiUrl}/ads`;

@Injectable({
    providedIn: 'root'
})

export class AdService {

  constructor(private http: HttpClient, private authStorageService: AuthStorageService) { }
  
  public ads: Ad[] = [];
  public ad!: Ad;

  public user?: User;

  ngOnInit() {
    this.authStorageService.user$.subscribe((user) => {
      this.user = user;
    });
  }

    getAllAds(): Observable<Ad[]> {
      return this.http.get<Ad[]>(ADS_API + `/get/all`, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
          console.log("Fetched by service:", ads)
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
      return this.http.get<Ad[]>(ADS_API + `/user/${id}`, httpOptionsWithSkip404).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    searchAds(fromMusician: string, searchingMusician: string, styles: string[], location: string): Observable<Ad[]> {
      const url = ADS_API + `/search?from=${fromMusician}&searching=${searchingMusician}&styles=${styles}&location=${location}`;
      return this.http.get<Ad[]>(url, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    createAd(ad: AdCreation) {
      return this.http.post<AdCreation>(ADS_API + `/create`, ad, httpOptions);
    }

    updateAd(ad: AdCreation, id: number) {
      return this.http.put<AdCreation>(ADS_API + `/${id}`, ad, httpOptions);
    }

    deleteAd(id: number) {
      return this.http.delete(ADS_API + `/${id}`, httpOptions);
    }

    adOrRemoveFavorite(userId: string, adId: number) {
      return this.http.post(ADS_API + `/favorites/${userId}/${adId}`, httpOptions);
    }

    getUserFavorites(userId: string): Observable<Ad[]> {
      return this.http.get<Ad[]>(ADS_API + `/favorites/${userId}`, httpOptionsWithSkip404);
    }

}
