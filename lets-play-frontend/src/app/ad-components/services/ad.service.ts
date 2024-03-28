import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { BehaviorSubject, Observable, tap } from 'rxjs';

import { Ad } from '../models/ad.model';
import { AdCreation } from '../models/adCreation.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};

@Injectable({
    providedIn: 'root'
})

export class AdService {

  constructor(private http: HttpClient) { }
  
  baseUrl: String = `${environment.apiUrl}/ads`;


    ads: Ad[] = [];
    ad!: Ad;

    getAllAds(): Observable<Ad[]> {
      const url = this.baseUrl + `/get/all`;

      return this.http.get<Ad[]>(url, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    getAdById(id: number): Observable<Ad> {
      const url = `${this.baseUrl}/get/${id}`;
      return this.http.get<Ad>(url, httpOptions).pipe(
        tap((ad: Ad) => {
          this.ad = ad;
        })
      );
    }

    searchAds(search: string, musicianTypes: string[], location: string): Observable<Ad[]> {

      const url = `${this.baseUrl}/search?search=${search}&musicianTypes=${musicianTypes}&location=${location}`;
      return this.http.get<Ad[]>(url, httpOptions).pipe(
        tap((ads: Ad[]) => {
          this.ads = ads;
        })
      );
    }

    createAd(ad: AdCreation) {
      const url = this.baseUrl + `/create`;
      console.log(url, ad, httpOptions);
      return this.http.post<AdCreation>(url, ad, httpOptions);
    }

}
