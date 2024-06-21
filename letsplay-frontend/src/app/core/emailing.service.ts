import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

import { Ad } from './models/ad.model';
import { User } from 'src/app/authentication/models/user.model';
import { contactEmail } from './models/contactEmail.model';

import { AuthStorageService } from 'src/app/shared/services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class EmailingService {

  constructor(private http: HttpClient, private authStorageService: AuthStorageService) { }

  protected fromUser?: User;

  ngOnInit() {
    this.authStorageService.user$.subscribe((user) => {
      this.fromUser = user;
    });
  }

  sendEmail(ad: Ad, toUser: string, message: string) {
    const emailData: contactEmail = {
      ad: ad,
      fromUser: this.fromUser!.id,
      toUser: ad.postedBy, // TODO : vérifier ce qui est renvoyé par le back
      messageContent: message
    };
    return this.http.post(environment.apiUrl + '/email/send', emailData);
  }
}
