import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { Ad } from '../../core/models/ad.model';
import { User } from 'src/app/authentication/models/user.model';
import { contactEmail } from '../../core/models/contactEmail.model';

import { AuthStorageService } from 'src/app/shared/services/storage.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  withCredentials: true
};


@Injectable({
  providedIn: 'root'
})
export class EmailingService {
  protected fromUser!: User;

  constructor(private http: HttpClient, private authStorageService: AuthStorageService) {
    this.fromUser = this.authStorageService.getUser();
    console.log('User:', this.fromUser)
   }

  sendEmail(ad: Ad, message: string) {
    console.log('Ad to send email:', ad)
    console.log("User sending email:", this.fromUser?.id, this.fromUser?.name)
    const emailData: contactEmail = {
      adId: ad.id,
      fromUser: this.fromUser!.id,
      messageContent: message
    };
    console.log('Email data:', emailData)
    return this.http.post(environment.apiUrl + '/contact', emailData, httpOptions);
  }
}
