import { Component, OnInit, Input } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';

import { MessageService } from 'primeng/api';

import { Ad } from '../models/ad.model';
import { AdService } from '../ad.service';
import { AuthStorageService } from '../../shared/services/storage.service';

import { musicStylesEnum } from '../enums/musicStylesEnum';
import { User } from 'src/app/authentication/models/user.model';

@Component({
  selector: 'app-ad',
  templateUrl: './ad.component.html',
  styleUrls: ['./ad.component.css']
})
export class AdComponent implements OnInit {
  @Input()
  public ad!: Ad;
  @Input()
  protected truncated = true;
  protected connectedUser!: User;
  protected isSingleAd!: boolean;
  protected musicStylesEnum = musicStylesEnum;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private adService: AdService,
    private _location: Location,
    private authStorageService: AuthStorageService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.authStorageService.user$.subscribe((user) => {
      this.connectedUser = user;
    });

    const currentRoute = this.route.snapshot.routeConfig?.path;
    if (currentRoute === 'home') {
      this.isSingleAd = false;
      this.ad.description = this.truncateText(this.ad.description);
    }

    if (currentRoute === 'ad/:id') {
      this.isSingleAd = true;
      const adId = this.route.snapshot.params['id'];
      this.adService.getAdById(adId).subscribe((ad: Ad) => {
        this.ad = ad;
      });
    }

    this.ad.styles = this.ad.styles.map(
      (style: string) => (musicStylesEnum as any)[style]
    );
  }

  truncateText(text: string): string {
    const words = text.split(' ');
    if (words.length <= 30) {
      return text;
    }
    const truncatedText = words.slice(0, 30);
    return truncatedText.join(' ') + '...';
  }

  goToAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}`);
  }

  isAuthorUser() {
    if(this.connectedUser) {
      return this.connectedUser.username === this.ad.postedBy;
    }
    else {
      return false;
    }
  }

  updateAd() {
    this.router.navigateByUrl(`ad/${this.ad.id}/update`);
  }

  back() {
    this._location.back();
  }

  contact() {
    if (Object.keys(this.connectedUser).length === 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erreur',
        detail: 'Tu dois être connecté pour répondre à une annonce',
      });
    } else {
      this.router.navigateByUrl('/contact');
    }
  }
}
